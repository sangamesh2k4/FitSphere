package com.sangamesh.Fitsphere.dto.contact;

import com.sangamesh.Fitsphere.enums.ContactMessageStatus;
import com.sangamesh.Fitsphere.enums.ContactReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactMessageResponseDto {

    private Long id;
    private String email;
    private ContactReason reason;
    private String message;
    private ContactMessageStatus status;
    private LocalDateTime createdAt;
}