package com.example.demo.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {
    @Autowired
    private AnnouncementRepository announcementRepository;
    @GetMapping
    public List<Announcement> getAllAnnouncements(){
        return announcementRepository.findAllByOrderByCreatedAtDesc();

    }
    @PostMapping
    public ResponseEntity<Announcement> createAnnouncement(@RequestBody Announcement announcement){
        Announcement saved = announcementRepository.save(announcement);
        return ResponseEntity.ok(saved);
    }

    @PatchMapping
    @Transactional
    public ResponseEntity<Announcement> editAnnouncement(@RequestBody EditAnnouncementRequest request) {
        String title = request.getTitle();
        if (title == null || title.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Announcement> announcementOptional = announcementRepository.findById(title);
        if (announcementOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Announcement announcement = announcementOptional.get();
        String correctedTitle = request.getCorrectedTitle();
        String correctedContent = request.getCorrectedContent();

        if (correctedTitle == null || correctedTitle.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (title.equals(correctedTitle)) {
            announcement.setContent(correctedContent);
            return ResponseEntity.ok(announcementRepository.save(announcement));
        }

        Announcement updated = new Announcement();
        updated.setTitle(correctedTitle);
        updated.setContent(correctedContent);
        updated.setPublisher(announcement.getPublisher());
        updated.setPublisherName(announcement.getPublisherName());
        updated.setCreatedAt(announcement.getCreatedAt());

        announcementRepository.delete(announcement);
        announcementRepository.flush();
        return ResponseEntity.ok(announcementRepository.save(updated));
    }
}
