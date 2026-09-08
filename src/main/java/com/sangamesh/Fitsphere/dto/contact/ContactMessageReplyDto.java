package com.sangamesh.Fitsphere.dto.contact;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactMessageReplyDto {

    @NotBlank(message = "Message cannot be empty")
    private String message;
}