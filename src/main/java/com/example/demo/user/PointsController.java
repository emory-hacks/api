package com.example.demo.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PointsController {

    @Autowired
    private QrCodeService qrCodeService;
    @Autowired
    private ScanTokenRepository scanTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventClaimRepository eventClaimRepository;
    @GetMapping(value = "/users/me/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrCode(Principal principal){
        try{
            String userEmail = principal.getName();
            byte[] qrImage = qrCodeService.generateUserQrCode(userEmail);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrImage);

        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/award-points-fast")
    public ResponseEntity<Map<String, Object>> awardPointsFast(@RequestBody AwardPointsRequest request){
        ScanToken scanToken = scanTokenRepository.findById(request.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid QR Code"));
        if(scanToken.isUsed() || scanToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "QR Code is already used or expired.");
        }
        String userEmail = scanToken.getUserEmail();
        String eventId = request.getEventId();
        if(eventClaimRepository.existsByUserEmailAndEventId(userEmail, eventId)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User has already recieved points for this event");

        }
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setPoints(user.getPoints()+request.getAmount());
        userRepository.save(user);

        EventClaim claim = new EventClaim();
        claim.setUserEmail(userEmail);
        claim.setEventId(eventId);
        claim.setPointsAwarded(request.getAmount());
        eventClaimRepository.save(claim);

        scanToken.setUsed(true);
        scanTokenRepository.save(scanToken);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("user", userEmail);
        response.put("pointsAdded", request.getAmount());
        response.put("newBalance", user.getPoints());
        return ResponseEntity.ok(response);





    }



}
