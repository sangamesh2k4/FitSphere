package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.dto.admin.AdminExerciseRequestDto;
import com.sangamesh.Fitsphere.dto.admin.AdminExerciseUpdateRequestDto;
import com.sangamesh.Fitsphere.dto.admin.AdminUsageStatsResponseDto;
import com.sangamesh.Fitsphere.dto.admin.AdminUserSummaryDto;
import com.sangamesh.Fitsphere.dto.contact.ContactMessageReplyDto;
import com.sangamesh.Fitsphere.dto.contact.ContactMessageResponseDto;
import com.sangamesh.Fitsphere.dto.contact.ContactMessageStatusUpdateDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseDetailDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseSummaryDto;
import com.sangamesh.Fitsphere.enums.*;
import com.sangamesh.Fitsphere.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin")
public class AdminController {

    private final AdminService adminService;

    AdminController(AdminService adminService){
        this.adminService=adminService;
    }

    @GetMapping("/test")
    @Operation(summary = "Admin Test Endpoint")
    public String test() {
        return "Welcome Admin!";
    }

    @PostMapping("/exercises")
    public ExerciseDetailDto addExercise(@Valid @RequestBody AdminExerciseRequestDto request) {
        return adminService.addExercise(request);
    }

    @GetMapping("/exercises")
    public Page<ExerciseSummaryDto> getAllExercises(@RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) Category category,
                                                    @RequestParam(required = false) Muscle primaryMuscle,
                                                    @RequestParam(required = false) Equipment equipment,
                                                    @RequestParam(required = false) Difficulty difficulty,
                                                    @RequestParam(required = false) ExerciseType exerciseType,
                                                    @RequestParam(required = false) Boolean active,
                                                    @RequestParam(defaultValue = "name") String sortBy,
                                                    @RequestParam(defaultValue = "asc") String direction,
                                                    @RequestParam(defaultValue = "0") int page) {

        return adminService.getAllExercises(keyword, category, primaryMuscle, equipment,
                difficulty, exerciseType, active, sortBy, direction, page);
    }

    @GetMapping("/exercises/{exerciseId}")
    public ExerciseDetailDto getExerciseById(@PathVariable Long exerciseId) {
        return adminService.getExerciseById(exerciseId);
    }

    @PatchMapping("/exercises/{exerciseId}")
    public ExerciseDetailDto updateExercise(@PathVariable Long exerciseId, @Valid @RequestBody AdminExerciseUpdateRequestDto request) {
        return adminService.updateExercise(exerciseId, request);
    }

    @PatchMapping("/exercises/{exerciseId}/disable")
    public String disableExercise(@PathVariable Long exerciseId) {
        adminService.disableExercise(exerciseId);
        return "Exercise disabled successfully";
    }

    @PatchMapping("/exercises/{exerciseId}/enable")
    public String enableExercise(@PathVariable Long exerciseId) {
        adminService.enableExercise(exerciseId);
        return "Exercise enabled successfully";
    }

    @GetMapping("/usage")
    public AdminUsageStatsResponseDto getUsageStats() {
        return adminService.getUsageStats();
    }

    @GetMapping("/users")
    public Page<AdminUserSummaryDto> getAllUsers(@RequestParam(defaultValue = "0") int page) {
        return adminService.getAllUsers(page);
    }
    //users//
    @PatchMapping("/users/{userId}/disable")
    public String disableUser(@PathVariable Long userId) {
        adminService.disableUser(userId);
        return "User suspended successfully";
    }

    @PatchMapping("/users/{userId}/enable")
    public String enableUser(@PathVariable Long userId) {
        adminService.enableUser(userId);
        return "User enabled successfully";
    }

    @Operation(summary = "Delete user account")
    @DeleteMapping("/users/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return "User deleted successfully";
    }

    //contact
    @GetMapping("/contact-messages")
    public Page<ContactMessageResponseDto> getContactMessages(@RequestParam(required = false) ContactMessageStatus status,
                                                              @RequestParam(defaultValue = "createdAt") String sortBy,
                                                              @RequestParam(defaultValue = "desc") String direction,
                                                              @RequestParam(defaultValue = "0") int page) {
        return adminService.getContactMessages(status, sortBy, direction, page);
    }

    @PostMapping("/contact-messages/{messageId}/reply")
    public String replyToContactMessage(@PathVariable Long messageId, @Valid @RequestBody ContactMessageReplyDto request) {
        adminService.replyToContactMessage(messageId, request.getMessage());
        return "Reply sent successfully";
    }

    @PatchMapping("/contact-messages/{messageId}/status")
    public String updateContactMessageStatus(@PathVariable Long messageId, @Valid @RequestBody ContactMessageStatusUpdateDto request) {
        adminService.updateContactMessageStatus(messageId, request.getStatus());
        return "Contact message status updated successfully";
    }
    @DeleteMapping("/contact-messages/{messageId}")
    public String deleteContactMessage(@PathVariable Long messageId) {
        adminService.deleteContactMessage(messageId);
        return "Contact message deleted successfully";
    }
    @GetMapping("/contact-messages/{messageId}")
    public ContactMessageResponseDto getContactMessageById(@PathVariable Long messageId) {
        return adminService.getContactMessageById(messageId);
    }
}
