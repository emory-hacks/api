package com.example.demo.user;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AnnouncementRepository extends JpaRepository<Announcement, Long>{
    List<Announcement> findAllByOrderByCreatedAtDesc();
}
