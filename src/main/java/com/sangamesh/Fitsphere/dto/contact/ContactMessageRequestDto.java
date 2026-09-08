package com.sangamesh.Fitsphere.dto.contact;

import com.sangamesh.Fitsphere.enums.ContactReason;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactMessageRequestDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotNull(message = "Contact reason is required")
    private ContactReason reason;

    private String message;
}