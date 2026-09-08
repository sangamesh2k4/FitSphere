package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.dto.admin.AdminExerciseRequestDto;
import com.sangamesh.Fitsphere.dto.admin.AdminExerciseUpdateRequestDto;
import com.sangamesh.Fitsphere.dto.admin.AdminUserSummaryDto;
import com.sangamesh.Fitsphere.dto.contact.ContactMessageResponseDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseDetailDto;
import com.sangamesh.Fitsphere.dto.admin.AdminUsageStatsResponseDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseSummaryDto;
import com.sangamesh.Fitsphere.enums.*;
import org.springframework.data.domain.Page;

public interface AdminService {



    ExerciseDetailDto addExercise(
            AdminExerciseRequestDto request);

    ExerciseDetailDto updateExercise(Long exerciseId, AdminExerciseUpdateRequestDto request);

    void disableExercise(Long exerciseId);

    void enableExercise(Long exerciseId);

    //users//
    void deleteUser(Long userId);
    void disableUser(Long userId);
    Page<AdminUserSummaryDto> getAllUsers(int page);

    void enableUser(Long userId);

    AdminUsageStatsResponseDto getUsageStats();



    ExerciseDetailDto getExerciseById(Long exerciseId);
    Page<ExerciseSummaryDto> getAllExercises(String keyword, Category category, Muscle primaryMuscle,
                                                    Equipment equipment, Difficulty difficulty, ExerciseType exerciseType,
                                                    Boolean active, String sortBy, String direction, int page);

    //contact
    Page<ContactMessageResponseDto> getContactMessages(ContactMessageStatus status, String sortBy, String direction, int page);
    void replyToContactMessage(Long messageId, String message);
    void updateContactMessageStatus(Long messageId, ContactMessageStatus status);
    void deleteContactMessage(Long messageId);
    ContactMessageResponseDto getContactMessageById(Long messageId);
}