package com.example.demo.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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

}
