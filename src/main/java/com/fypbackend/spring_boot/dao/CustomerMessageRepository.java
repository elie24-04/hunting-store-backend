package com.fypbackend.spring_boot.dao;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fypbackend.spring_boot.entity.CustomerMessage;
import com.fypbackend.spring_boot.entity.MessageStatus;
import com.fypbackend.spring_boot.entity.MessageType;

public interface CustomerMessageRepository extends JpaRepository<CustomerMessage, UUID>, JpaSpecificationExecutor<CustomerMessage> {

    Page<CustomerMessage> findByStatus(MessageStatus status, Pageable pageable);

    Page<CustomerMessage> findByType(MessageType type, Pageable pageable);

    @Query("SELECT cm FROM CustomerMessage cm "
            + "WHERE (:keyword IS NULL OR TRIM(:keyword) = '' OR "
            + "LOWER(cm.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(cm.message) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(cm.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(cm.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<CustomerMessage> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
