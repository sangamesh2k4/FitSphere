package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.dto.contact.ContactMessageRequestDto;

public interface ContactMessageService {

    void submit(ContactMessageRequestDto request);
}
