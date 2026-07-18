package com.example.demo.user;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
 interface ScanTokenRepository extends JpaRepository<ScanToken, UUID> {}
public interface EventClaimRepository extends JpaRepository<EventClaim, Long>{
     boolean existsByUserEmailAndEventId(String userEmail, String eventId);

}
