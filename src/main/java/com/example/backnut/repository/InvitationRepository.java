package com.example.backnut.repository;

import com.example.backnut.models.Invitation;
import com.example.backnut.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    java.util.List<Invitation> findByReceiver(User receiver);
    Invitation findBySenderAndReceiver(User sender, User receiver);
    @Query("SELECT i FROM Invitation i WHERE i.status = :status AND (i.sender.id = :userId OR i.receiver.id = :userId)")
    List<Invitation> findAcceptedInvitationsForUser(@Param("status") String status, @Param("userId") Long userId);
}
