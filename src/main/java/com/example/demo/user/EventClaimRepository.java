package com.example.demo.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;
 interface ScanTokenRepository extends JpaRepository<ScanToken, UUID> {}
public interface EventClaimRepository extends JpaRepository<EventClaim, Long>{
     @Query("SELECT COUNT(c) > 0 FROM EventClaim c WHERE c.userEmail = :userEmail AND c.eventId = :eventId")
     boolean existsByUserEmailAndEventId(@Param("userEmail") String userEmail, @Param("eventId") String eventId);

}
