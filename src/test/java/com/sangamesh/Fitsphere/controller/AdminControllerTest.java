package com.sangamesh.Fitsphere.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangamesh.Fitsphere.dto.admin.*;
import com.sangamesh.Fitsphere.dto.contact.*;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseDetailDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseSummaryDto;
import com.sangamesh.Fitsphere.enums.*;
import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.AdminService;
import com.sangamesh.Fitsphere.service.CustomUserDetailsService;
import com.sangamesh.Fitsphere.service.UsageTrackingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private UsageTrackingService usageTrackingService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void test_shouldReturnWelcomeMessage() throws Exception {

        mockMvc.perform(get("/api/admin/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome Admin!"));
    }

    @Test
    void addExercise_shouldReturnExercise() throws Exception {

        AdminExerciseRequestDto request = new AdminExerciseRequestDto();

        request.setName("Bench Press");
        request.setCategory(Category.CHEST);
        request.setPrimaryMuscle(Muscle.MIDDLE_CHEST);
        request.setMovementPattern(MovementPattern.HORIZONTAL_PUSH);
        request.setEquipment(Equipment.BARBELL);
        request.setExerciseType(ExerciseType.COMPOUND);
        request.setDifficulty(Difficulty.INTERMEDIATE);
        request.setInstructions(List.of("Step 1"));

        ExerciseDetailDto response = new ExerciseDetailDto();

        when(adminService.addExercise(any(AdminExerciseRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/admin/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(adminService).addExercise(any(AdminExerciseRequestDto.class));
    }

    @Test
    void getAllExercises_shouldReturnPage() throws Exception {

        when(adminService.getAllExercises(
                any(), any(), any(), any(), any(), any(),
                any(), anyString(), anyString(), anyInt()
        )).thenReturn(new PageImpl<>(List.of(new ExerciseSummaryDto())));

        mockMvc.perform(get("/api/admin/exercises"))
                .andExpect(status().isOk());

        verify(adminService).getAllExercises(
                isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(),
                eq("name"), eq("asc"), eq(0)
        );
    }

    @Test
    void getAllExercises_shouldPassFilters() throws Exception {

        when(adminService.getAllExercises(
                any(), any(), any(), any(), any(), any(),
                any(), anyString(), anyString(), anyInt()
        )).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/admin/exercises")
                        .param("keyword", "bench")
                        .param("category", "CHEST")
                        .param("primaryMuscle", "MIDDLE_CHEST")
                        .param("equipment", "BARBELL")
                        .param("difficulty", "INTERMEDIATE")
                        .param("exerciseType", "COMPOUND")
                        .param("active", "true")
                        .param("sortBy", "name")
                        .param("direction", "desc")
                        .param("page", "2"))
                .andExpect(status().isOk());

        verify(adminService).getAllExercises(
                eq("bench"),
                eq(Category.CHEST),
                eq(Muscle.MIDDLE_CHEST),
                eq(Equipment.BARBELL),
                eq(Difficulty.INTERMEDIATE),
                eq(ExerciseType.COMPOUND),
                eq(true),
                eq("name"),
                eq("desc"),
                eq(2)
        );
    }

    @Test
    void getExerciseById_shouldReturnExercise() throws Exception {

        ExerciseDetailDto response = new ExerciseDetailDto();

        when(adminService.getExerciseById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/admin/exercises/1"))
                .andExpect(status().isOk());

        verify(adminService).getExerciseById(1L);
    }

    @Test
    void updateExercise_shouldReturnExercise() throws Exception {

        AdminExerciseUpdateRequestDto request =
                new AdminExerciseUpdateRequestDto();

        request.setName("Updated Bench Press");

        when(adminService.updateExercise(
                eq(1L),
                any(AdminExerciseUpdateRequestDto.class)
        )).thenReturn(new ExerciseDetailDto());

        mockMvc.perform(patch("/api/admin/exercises/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(adminService).updateExercise(
                eq(1L),
                any(AdminExerciseUpdateRequestDto.class)
        );
    }

    @Test
    void disableExercise_shouldReturnSuccessMessage() throws Exception {

        doNothing().when(adminService).disableExercise(1L);

        mockMvc.perform(patch("/api/admin/exercises/1/disable"))
                .andExpect(status().isOk())
                .andExpect(content().string("Exercise disabled successfully"));

        verify(adminService).disableExercise(1L);
    }

    @Test
    void enableExercise_shouldReturnSuccessMessage() throws Exception {

        doNothing().when(adminService).enableExercise(1L);

        mockMvc.perform(patch("/api/admin/exercises/1/enable"))
                .andExpect(status().isOk())
                .andExpect(content().string("Exercise enabled successfully"));

        verify(adminService).enableExercise(1L);
    }

    @Test
    void getUsageStats_shouldReturnStats() throws Exception {

        when(adminService.getUsageStats())
                .thenReturn(new AdminUsageStatsResponseDto());

        mockMvc.perform(get("/api/admin/usage"))
                .andExpect(status().isOk());

        verify(adminService).getUsageStats();
    }

    @Test
    void getAllUsers_shouldReturnPage() throws Exception {

        when(adminService.getAllUsers(0))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());

        verify(adminService).getAllUsers(0);
    }

    @Test
    void getAllUsers_shouldPassPage() throws Exception {

        when(adminService.getAllUsers(3))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/admin/users")
                        .param("page", "3"))
                .andExpect(status().isOk());

        verify(adminService).getAllUsers(3);
    }

    @Test
    void disableUser_shouldReturnSuccessMessage() throws Exception {

        mockMvc.perform(patch("/api/admin/users/5/disable"))
                .andExpect(status().isOk())
                .andExpect(content().string("User suspended successfully"));

        verify(adminService).disableUser(5L);
    }

    @Test
    void enableUser_shouldReturnSuccessMessage() throws Exception {

        mockMvc.perform(patch("/api/admin/users/5/enable"))
                .andExpect(status().isOk())
                .andExpect(content().string("User enabled successfully"));

        verify(adminService).enableUser(5L);
    }

    @Test
    void deleteUser_shouldReturnSuccessMessage() throws Exception {

        mockMvc.perform(delete("/api/admin/users/5"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));

        verify(adminService).deleteUser(5L);
    }

    @Test
    void getContactMessages_shouldReturnPage() throws Exception {

        when(adminService.getContactMessages(
                any(), anyString(), anyString(), anyInt()
        )).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/admin/contact-messages"))
                .andExpect(status().isOk());

        verify(adminService).getContactMessages(
                isNull(), eq("createdAt"), eq("desc"), eq(0)
        );
    }

    @Test
    void getContactMessages_shouldPassFilters() throws Exception {

        when(adminService.getContactMessages(
                any(), anyString(), anyString(), anyInt()
        )).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/admin/contact-messages")
                        .param("status", "OPEN")
                        .param("sortBy", "createdAt")
                        .param("direction", "asc")
                        .param("page", "2"))
                .andExpect(status().isOk());

        verify(adminService).getContactMessages(
                eq(ContactMessageStatus.OPEN),
                eq("createdAt"),
                eq("asc"),
                eq(2)
        );
    }

    @Test
    void replyToContactMessage_shouldReturnSuccessMessage() throws Exception {

        ContactMessageReplyDto request = new ContactMessageReplyDto();
        request.setMessage("Your issue has been resolved.");

        mockMvc.perform(post("/api/admin/contact-messages/1/reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Reply sent successfully"));

        verify(adminService).replyToContactMessage(
                1L,
                "Your issue has been resolved."
        );
    }

    @Test
    void updateContactMessageStatus_shouldReturnSuccessMessage()
            throws Exception {

        ContactMessageStatusUpdateDto request =
                new ContactMessageStatusUpdateDto();

        request.setStatus(ContactMessageStatus.RESOLVED);

        mockMvc.perform(patch("/api/admin/contact-messages/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Contact message status updated successfully"
                ));

        verify(adminService).updateContactMessageStatus(
                1L,
                ContactMessageStatus.RESOLVED
        );
    }

    @Test
    void deleteContactMessage_shouldReturnSuccessMessage() throws Exception {

        mockMvc.perform(delete("/api/admin/contact-messages/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Contact message deleted successfully"
                ));

        verify(adminService).deleteContactMessage(1L);
    }

    @Test
    void getContactMessageById_shouldReturnMessage() throws Exception {

        when(adminService.getContactMessageById(1L))
                .thenReturn(new ContactMessageResponseDto());

        mockMvc.perform(get("/api/admin/contact-messages/1"))
                .andExpect(status().isOk());

        verify(adminService).getContactMessageById(1L);
    }
}