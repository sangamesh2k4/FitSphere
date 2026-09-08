package com.sangamesh.Fitsphere.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangamesh.Fitsphere.dto.contact.ContactMessageRequestDto;
import com.sangamesh.Fitsphere.enums.ContactReason;
import com.sangamesh.Fitsphere.ratelimit.RateLimitFilter;
import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.security.JwtAuthenticationFilter;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.ContactMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactMessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContactMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ContactMessageService contactMessageService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private RateLimitFilter rateLimitFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void submitContactMessage_shouldReturnSuccess() throws Exception {

        ContactMessageRequestDto request = new ContactMessageRequestDto();
        request.setEmail("user@example.com");
        request.setReason(ContactReason.BUG_REPORT);
        request.setMessage("There is a bug.");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Message sent successfully"));

        verify(contactMessageService).submit(any(ContactMessageRequestDto.class));
    }

    @Test
    void submitContactMessage_shouldPassRequestToService() throws Exception {

        ContactMessageRequestDto request = new ContactMessageRequestDto();
        request.setEmail("user@example.com");
        request.setReason(ContactReason.ACCOUNT_ISSUE);
        request.setMessage("I cannot access my account.");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(contactMessageService).submit(argThat(r ->
                r.getEmail().equals("user@example.com")
                        && r.getReason() == ContactReason.ACCOUNT_ISSUE
                        && r.getMessage().equals("I cannot access my account.")
        ));
    }

    @Test
    void submitContactMessage_whenValidationFails_shouldReturn400() throws Exception {

        ContactMessageRequestDto request = new ContactMessageRequestDto();
        request.setEmail("invalid-email");
        request.setReason(ContactReason.BUG_REPORT);
        request.setMessage("There is a bug.");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contactMessageService);
    }

    @Test
    void submitContactMessage_whenBodyIsMissing_shouldReturn400() throws Exception {

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contactMessageService);
    }
}