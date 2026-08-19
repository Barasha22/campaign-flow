package com.experiment.campaignflow.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.experiment.campaignflow.domain.RecipientList;

public interface RecipientListRepository
        extends JpaRepository<RecipientList, UUID> {

    boolean existsByName(String name);

    @Modifying
    @Query("""
                UPDATE RecipientList rl
                SET rl.recipientCount =
                    rl.recipientCount + :amount
                WHERE rl.id = :recipientListId
            """)
    int incrementRecipientCount(
            @Param("recipientListId") UUID recipientListId,
            @Param("amount") long amount);
}