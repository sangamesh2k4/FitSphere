package com.sangamesh.Fitsphere.dto.contact;

import com.sangamesh.Fitsphere.enums.ContactMessageStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactMessageStatusUpdateDto {

    @NotNull(message = "Status is required")
    private ContactMessageStatus status;
}