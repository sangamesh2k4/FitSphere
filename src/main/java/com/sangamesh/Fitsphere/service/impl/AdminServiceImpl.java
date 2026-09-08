package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.dto.admin.AdminExerciseRequestDto;
import com.sangamesh.Fitsphere.dto.admin.AdminExerciseUpdateRequestDto;
import com.sangamesh.Fitsphere.dto.admin.AdminUsageStatsResponseDto;
import com.sangamesh.Fitsphere.dto.admin.AdminUserSummaryDto;
import com.sangamesh.Fitsphere.dto.contact.ContactMessageResponseDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseDetailDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseSummaryDto;
import com.sangamesh.Fitsphere.entity.*;
import com.sangamesh.Fitsphere.enums.*;
import com.sangamesh.Fitsphere.exception.ResourceNotFoundException;
import com.sangamesh.Fitsphere.mapper.ExerciseMapper;
import com.sangamesh.Fitsphere.repository.ContactMessageRepository;
import com.sangamesh.Fitsphere.repository.ExerciseRepository;
import com.sangamesh.Fitsphere.repository.FoodLogRepository;
import com.sangamesh.Fitsphere.repository.UserRepository;
import com.sangamesh.Fitsphere.service.AdminService;
import com.sangamesh.Fitsphere.service.EmailService;
import com.sangamesh.Fitsphere.service.RefreshTokenService;
import com.sangamesh.Fitsphere.service.UsageTrackingService;
import com.sangamesh.Fitsphere.specification.ContactMessageSpecification;
import com.sangamesh.Fitsphere.specification.ExerciseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final ExerciseRepository exerciseRepository;

    private final UserRepository userRepository;

    private final FoodLogRepository foodLogRepository;

    private final ContactMessageRepository contactMessageRepository;

    private final ExerciseMapper exerciseMapper;

    private final UsageTrackingService usageTrackingService;

    private final RefreshTokenService refreshTokenService;

    private final EmailService emailService;


    @Value("${exercise.pagination.page-size}")
    private int pageSize;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("name", "category", "primaryMuscle",
            "equipment", "difficulty", "exerciseType");

    @Override
    public ExerciseDetailDto addExercise(
            AdminExerciseRequestDto request) {

        Exercise exercise = Exercise.builder()
                .name(request.getName())
                .category(request.getCategory())
                .primaryMuscle(request.getPrimaryMuscle())
                .secondaryMuscles(request.getSecondaryMuscles())
                .movementPattern(request.getMovementPattern())
                .equipment(request.getEquipment())
                .exerciseType(request.getExerciseType())
                .difficulty(request.getDifficulty())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .active(true)
                .build();

        List<ExerciseInstruction> instructions = IntStream.range(0,
                        request.getInstructions() == null
                                        ? 0
                                        : request.getInstructions().size())
                        .mapToObj(index ->
                                ExerciseInstruction.builder()
                                        .stepNumber(index + 1)
                                        .instruction(request.getInstructions().get(index))
                                        .exercise(exercise)
                                        .build())
                .toList();

        List<ExerciseTip> tips =
                request.getTips() == null
                        ? List.of()
                        : request.getTips()
                        .stream()
                        .map(tip ->
                                ExerciseTip.builder()
                                        .tip(tip)
                                        .exercise(exercise)
                                        .build())
                        .toList();

        List<ExerciseCommonMistake> commonMistakes =
                request.getCommonMistakes() == null
                        ? List.of()
                        : request.getCommonMistakes()
                        .stream()
                        .map(mistake ->
                                ExerciseCommonMistake.builder()
                                        .mistake(mistake)
                                        .exercise(exercise)
                                        .build())
                        .toList();

        exercise.setInstructions(instructions);
        exercise.setTips(tips);
        exercise.setCommonMistakes(commonMistakes);

        Exercise savedExercise = exerciseRepository.save(exercise);

        return exerciseMapper.toDetailDto(savedExercise);
    }


    @Override
    public ExerciseDetailDto updateExercise(Long exerciseId, AdminExerciseUpdateRequestDto request) {

        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(() ->
                        new ResourceNotFoundException("Exercise not found with id: " + exerciseId));

        if (request.getName() != null) {
            exercise.setName(request.getName());
        }
        if (request.getCategory() != null) {
            exercise.setCategory(request.getCategory());
        }
        if (request.getPrimaryMuscle() != null) {
            exercise.setPrimaryMuscle(request.getPrimaryMuscle());
        }
        if (request.getSecondaryMuscles() != null) {
            exercise.setSecondaryMuscles(request.getSecondaryMuscles());
        }
        if (request.getMovementPattern() != null) {
            exercise.setMovementPattern(request.getMovementPattern());
        }
        if (request.getEquipment() != null) {
            exercise.setEquipment(request.getEquipment());
        }
        if (request.getExerciseType() != null) {
            exercise.setExerciseType(request.getExerciseType());
        }
        if (request.getDifficulty() != null) {
            exercise.setDifficulty(request.getDifficulty());
        }
        if (request.getDescription() != null) {
            exercise.setDescription(request.getDescription());
        }
        if (request.getImageUrl() != null) {
            exercise.setImageUrl(request.getImageUrl());
        }
        // instructions
        if (request.getInstructions() != null) {
            exercise.getInstructions().clear();
            IntStream.range(0, request.getInstructions().size())
                    .forEach(index ->
                            exercise.getInstructions().add(
                                    ExerciseInstruction.builder()
                                            .stepNumber(index + 1)
                                            .instruction(request.getInstructions().get(index))
                                            .exercise(exercise)
                                            .build()
                            ));
        }
        // tips
        if (request.getTips() != null) {
            exercise.getTips().clear();
            request.getTips().forEach(tip ->
                    exercise.getTips().add(
                            ExerciseTip.builder()
                                    .tip(tip)
                                    .exercise(exercise)
                                    .build()
                    ));
        }
        // common mistakes
        if (request.getCommonMistakes() != null) {
            exercise.getCommonMistakes().clear();
            request.getCommonMistakes().forEach(mistake ->
                    exercise.getCommonMistakes().add(
                            ExerciseCommonMistake.builder()
                                    .mistake(mistake)
                                    .exercise(exercise)
                                    .build()
                    ));
        }

        Exercise updatedExercise = exerciseRepository.save(exercise);
        return exerciseMapper.toDetailDto(updatedExercise);
    }

    @Override
    public void disableExercise(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(() ->
                        new ResourceNotFoundException("Exercise not found with id: " + exerciseId));
        exercise.setActive(false);
        exerciseRepository.save(exercise);
    }

    @Override
    public void enableExercise(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(() ->
                        new ResourceNotFoundException("Exercise not found with id: " + exerciseId));
        exercise.setActive(true);
        exerciseRepository.save(exercise);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + userId));
        userRepository.delete(user);
    }

    @Override
    public AdminUsageStatsResponseDto getUsageStats() {
        AdminUsageStatsResponseDto stats = new AdminUsageStatsResponseDto();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalFoodLogs(foodLogRepository.count());
        stats.setTodayFoodLogs(foodLogRepository.countByLogDate(LocalDate.now()));
        stats.setTotalExercises(exerciseRepository.count());
        stats.setActiveExercises(exerciseRepository.countByActiveTrue());
        stats.setDisabledExercises(exerciseRepository.countByActiveFalse());
        stats.setTodayLoginRequests(usageTrackingService.getTodayUsage("login"));
        stats.setTodayRegistrationRequests(usageTrackingService.getTodayUsage("register"));
        stats.setTodayNutritionRequests(usageTrackingService.getTodayUsage("nutrition"));
        stats.setTodayYoutubeRequests(usageTrackingService.getTodayUsage("youtube"));
        return stats;
    }


    @Override
    public Page<AdminUserSummaryDto> getAllUsers(int page) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), 20, Sort.by("username").ascending());
        return userRepository.findAll(pageable).map(user -> {
                    AdminUserSummaryDto dto = new AdminUserSummaryDto();
                    dto.setId(user.getId());
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setRole(user.getRole());
                    dto.setEnabled(user.getEnabled());
                    return dto;
                });
    }

    @Override
    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setEnabled(false);
        userRepository.save(user);
        refreshTokenService.deleteAllUserTokens(userId);
    }

    @Override
    public void enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setEnabled(true);
        userRepository.save(user);
    }
    @Override
    public Page<ExerciseSummaryDto> getAllExercises(String keyword, Category category, Muscle primaryMuscle,
                                                    Equipment equipment, Difficulty difficulty, ExerciseType exerciseType,
                                                    Boolean active, String sortBy, String direction, int page) {

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "name";
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {
            direction = "asc";
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        page = Math.max(page, 0);

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Specification<Exercise> specification =
                ExerciseSpecification.hasKeyword(keyword)
                        .and(ExerciseSpecification.hasCategory(category))
                        .and(ExerciseSpecification.hasPrimaryMuscle(primaryMuscle))
                        .and(ExerciseSpecification.hasEquipment(equipment))
                        .and(ExerciseSpecification.hasDifficulty(difficulty))
                        .and(ExerciseSpecification.hasExerciseType(exerciseType))
                        .and(ExerciseSpecification.hasActive(active));
        return exerciseRepository
                .findAll(specification, pageable)
                .map(exerciseMapper::toSummaryDto);
    }

    @Override
    public ExerciseDetailDto getExerciseById(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Exercise not found"));
        return exerciseMapper.toDetailDto(exercise);
    }

    //contact
    @Override
    public Page<ContactMessageResponseDto> getContactMessages(ContactMessageStatus status, String sortBy, String direction, int page) {
        if (!sortBy.equals("createdAt")) {
            sortBy = "createdAt";
        }
        if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
            direction = "desc";
        }
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        page = Math.max(page, 0);
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<ContactMessage> specification = ContactMessageSpecification.hasStatus(status);
        return contactMessageRepository
                .findAll(specification, pageable)
                .map(contactMessage -> new ContactMessageResponseDto(contactMessage.getId(), contactMessage.getEmail(),
                        contactMessage.getReason(), contactMessage.getMessage(), contactMessage.getStatus(), contactMessage.getCreatedAt()));
    }

    @Override
    public void replyToContactMessage(Long messageId, String message) {
        ContactMessage contactMessage = contactMessageRepository.findById(messageId)
                        .orElseThrow(() -> new ResourceNotFoundException("Contact message not found with id: " + messageId));
        emailService.sendEmail(contactMessage.getEmail(), "FitSphere Support", message);
    }
    @Override
    public void updateContactMessageStatus(Long messageId, ContactMessageStatus status) {
        ContactMessage contactMessage = contactMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact message not found with id: " + messageId));
        contactMessage.setStatus(status);
        contactMessageRepository.save(contactMessage);
    }

    @Override
    public void deleteContactMessage(Long messageId) {
        if (!contactMessageRepository.existsById(messageId)) {
            throw new ResourceNotFoundException("Contact message not found with id: " + messageId
            );
        }
        contactMessageRepository.deleteById(messageId);
    }
    @Override
    public ContactMessageResponseDto getContactMessageById(Long messageId) {
        ContactMessage contactMessage = contactMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact message not found with id: " + messageId));
        return new ContactMessageResponseDto(contactMessage.getId(), contactMessage.getEmail(),
                contactMessage.getReason(), contactMessage.getMessage(), contactMessage.getStatus(), contactMessage.getCreatedAt());
    }
}
