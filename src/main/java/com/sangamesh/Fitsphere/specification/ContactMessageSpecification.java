package com.sangamesh.Fitsphere.specification;

import com.sangamesh.Fitsphere.entity.ContactMessage;
import com.sangamesh.Fitsphere.enums.ContactMessageStatus;
import org.springframework.data.jpa.domain.Specification;

public class ContactMessageSpecification {

    public static Specification<ContactMessage> hasStatus(
            ContactMessageStatus status) {

        return (root, query, criteriaBuilder) -> {

            if (status == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("status"), status);
        };
    }
}