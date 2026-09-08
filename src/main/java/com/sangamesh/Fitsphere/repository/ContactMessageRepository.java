package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long>,
        JpaSpecificationExecutor<ContactMessage> {
}