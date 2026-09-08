package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.dto.admin.AdminExerciseRequestDto;
import com.sangamesh.Fitsphere.dto.admin.AdminExerciseUpdateRequestDto;
import com.sangamesh.Fitsphere.dto.admin.AdminUserSummaryDto;
import com.sangamesh.Fitsphere.dto.admin.AdminUsageStatsResponseDto;
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
import com.sangamesh.Fitsphere.service.EmailService;
import com.sangamesh.Fitsphere.service.RefreshTokenService;
import com.sangamesh.Fitsphere.service.UsageTrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FoodLogRepository foodLogRepository;

    @Mock
    private ContactMessageRepository contactMessageRepository;

    @Mock
    private ExerciseMapper exerciseMapper;

    @Mock
    private UsageTrackingService usageTrackingService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private EmailService emailService;

    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminServiceImpl(
                exerciseRepository,
                userRepository,
                foodLogRepository,
                contactMessageRepository,
                exerciseMapper,
                usageTrackingService,
                refreshTokenService,
                emailService
        );

        ReflectionTestUtils.setField(adminService, "pageSize", 10);
    }

    // ---------------------------------------------------------
    // ADD EXERCISE
    // ---------------------------------------------------------

    @Test
    void addExercise_shouldCreateAndSaveExercise() {

        AdminExerciseRequestDto request = new AdminExerciseRequestDto();

        request.setName("Bench Press");
        request.setCategory(Category.CHEST);
        request.setPrimaryMuscle(Muscle.MIDDLE_CHEST);
        request.setSecondaryMuscles(List.of(Muscle.FRONT_DELTS));
        request.setMovementPattern(MovementPattern.HORIZONTAL_PUSH);
        request.setEquipment(Equipment.BARBELL);
        request.setExerciseType(ExerciseType.COMPOUND);
        request.setDifficulty(Difficulty.INTERMEDIATE);
        request.setDescription("Chest exercise");
        request.setImageUrl("bench.jpg");
        request.setInstructions(List.of(
                "Lie on the bench",
                "Lower the bar",
                "Press the bar"
        ));
        request.setTips(List.of("Keep your feet stable"));
        request.setCommonMistakes(List.of("Flaring elbows"));

        Exercise savedExercise = Exercise.builder()
                .id(1L)
                .name("Bench Press")
                .category(Category.CHEST)
                .primaryMuscle(Muscle.MIDDLE_CHEST)
                .movementPattern(MovementPattern.HORIZONTAL_PUSH)
                .equipment(Equipment.BARBELL)
                .exerciseType(ExerciseType.COMPOUND)
                .difficulty(Difficulty.INTERMEDIATE)
                .active(true)
                .build();

        ExerciseDetailDto expected = new ExerciseDetailDto();

        when(exerciseRepository.save(any(Exercise.class)))
                .thenReturn(savedExercise);

        when(exerciseMapper.toDetailDto(savedExercise))
                .thenReturn(expected);

        ExerciseDetailDto result = adminService.addExercise(request);

        assertSame(expected, result);

        ArgumentCaptor<Exercise> captor =
                ArgumentCaptor.forClass(Exercise.class);

        verify(exerciseRepository).save(captor.capture());

        Exercise saved = captor.getValue();

        assertEquals("Bench Press", saved.getName());
        assertEquals(Category.CHEST, saved.getCategory());
        assertEquals(Muscle.MIDDLE_CHEST, saved.getPrimaryMuscle());
        assertEquals(Equipment.BARBELL, saved.getEquipment());
        assertEquals(ExerciseType.COMPOUND, saved.getExerciseType());
        assertEquals(Difficulty.INTERMEDIATE, saved.getDifficulty());
        assertTrue(saved.getActive());

        assertEquals(3, saved.getInstructions().size());
        assertEquals(1, saved.getInstructions().get(0).getStepNumber());
        assertEquals(2, saved.getInstructions().get(1).getStepNumber());
        assertEquals(3, saved.getInstructions().get(2).getStepNumber());

        assertEquals("Press the bar",
                saved.getInstructions().get(2).getInstruction());

        assertEquals(1, saved.getTips().size());
        assertEquals("Keep your feet stable",
                saved.getTips().get(0).getTip());

        assertEquals(1, saved.getCommonMistakes().size());
        assertEquals("Flaring elbows",
                saved.getCommonMistakes().get(0).getMistake());

        verify(exerciseMapper).toDetailDto(savedExercise);
    }

    @Test
    void addExercise_whenListsAreNull_shouldCreateEmptyChildLists() {

        AdminExerciseRequestDto request = new AdminExerciseRequestDto();

        request.setName("Squat");
        request.setCategory(Category.LEGS);
        request.setPrimaryMuscle(Muscle.QUADRICEPS);
        request.setMovementPattern(MovementPattern.SQUAT);
        request.setEquipment(Equipment.BARBELL);
        request.setExerciseType(ExerciseType.COMPOUND);
        request.setDifficulty(Difficulty.ADVANCED);

        Exercise saved = Exercise.builder()
                .id(1L)
                .name("Squat")
                .active(true)
                .build();

        ExerciseDetailDto expected = new ExerciseDetailDto();

        when(exerciseRepository.save(any(Exercise.class)))
                .thenReturn(saved);
        when(exerciseMapper.toDetailDto(saved))
                .thenReturn(expected);

        adminService.addExercise(request);

        ArgumentCaptor<Exercise> captor =
                ArgumentCaptor.forClass(Exercise.class);

        verify(exerciseRepository).save(captor.capture());

        Exercise exercise = captor.getValue();

        assertNotNull(exercise.getInstructions());
        assertNotNull(exercise.getTips());
        assertNotNull(exercise.getCommonMistakes());

        assertTrue(exercise.getInstructions().isEmpty());
        assertTrue(exercise.getTips().isEmpty());
        assertTrue(exercise.getCommonMistakes().isEmpty());
    }

    // ---------------------------------------------------------
    // UPDATE EXERCISE
    // ---------------------------------------------------------

    @Test
    void updateExercise_shouldUpdateProvidedFields() {

        Exercise exercise = Exercise.builder()
                .id(1L)
                .name("Old Name")
                .category(Category.CHEST)
                .primaryMuscle(Muscle.MIDDLE_CHEST)
                .movementPattern(MovementPattern.HORIZONTAL_PUSH)
                .equipment(Equipment.BARBELL)
                .exerciseType(ExerciseType.COMPOUND)
                .difficulty(Difficulty.INTERMEDIATE)
                .instructions(new java.util.ArrayList<>())
                .tips(new java.util.ArrayList<>())
                .commonMistakes(new java.util.ArrayList<>())
                .build();

        AdminExerciseUpdateRequestDto request =
                new AdminExerciseUpdateRequestDto();

        request.setName("Updated Name");
        request.setCategory(Category.BACK);
        request.setPrimaryMuscle(Muscle.LATS);
        request.setEquipment(Equipment.DUMBBELL);
        request.setDifficulty(Difficulty.ADVANCED);
        request.setDescription("Updated description");
        request.setImageUrl("updated.jpg");
        request.setInstructions(List.of("Step one", "Step two"));
        request.setTips(List.of("New tip"));
        request.setCommonMistakes(List.of("New mistake"));

        ExerciseDetailDto expected = new ExerciseDetailDto();

        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        when(exerciseRepository.save(exercise))
                .thenReturn(exercise);

        when(exerciseMapper.toDetailDto(exercise))
                .thenReturn(expected);

        ExerciseDetailDto result =
                adminService.updateExercise(1L, request);

        assertSame(expected, result);

        assertEquals("Updated Name", exercise.getName());
        assertEquals(Category.BACK, exercise.getCategory());
        assertEquals(Muscle.LATS, exercise.getPrimaryMuscle());
        assertEquals(Equipment.DUMBBELL, exercise.getEquipment());
        assertEquals(Difficulty.ADVANCED, exercise.getDifficulty());
        assertEquals("Updated description", exercise.getDescription());
        assertEquals("updated.jpg", exercise.getImageUrl());

        assertEquals(2, exercise.getInstructions().size());
        assertEquals(1, exercise.getInstructions().get(0).getStepNumber());
        assertEquals(2, exercise.getInstructions().get(1).getStepNumber());

        assertEquals(1, exercise.getTips().size());
        assertEquals(1, exercise.getCommonMistakes().size());

        verify(exerciseRepository).save(exercise);
    }

    @Test
    void updateExercise_whenFieldsAreNull_shouldKeepExistingValues() {

        Exercise exercise = Exercise.builder()
                .id(1L)
                .name("Bench Press")
                .category(Category.CHEST)
                .primaryMuscle(Muscle.MIDDLE_CHEST)
                .movementPattern(MovementPattern.HORIZONTAL_PUSH)
                .equipment(Equipment.BARBELL)
                .exerciseType(ExerciseType.COMPOUND)
                .difficulty(Difficulty.INTERMEDIATE)
                .instructions(new java.util.ArrayList<>())
                .tips(new java.util.ArrayList<>())
                .commonMistakes(new java.util.ArrayList<>())
                .build();

        AdminExerciseUpdateRequestDto request =
                new AdminExerciseUpdateRequestDto();

        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        when(exerciseRepository.save(exercise))
                .thenReturn(exercise);

        when(exerciseMapper.toDetailDto(exercise))
                .thenReturn(new ExerciseDetailDto());

        adminService.updateExercise(1L, request);

        assertEquals("Bench Press", exercise.getName());
        assertEquals(Category.CHEST, exercise.getCategory());
        assertEquals(Muscle.MIDDLE_CHEST, exercise.getPrimaryMuscle());
        assertEquals(Equipment.BARBELL, exercise.getEquipment());

        verify(exerciseRepository).save(exercise);
    }

    @Test
    void updateExercise_whenNotFound_shouldThrowException() {

        AdminExerciseUpdateRequestDto request =
                new AdminExerciseUpdateRequestDto();

        when(exerciseRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> adminService.updateExercise(99L, request)
                );

        assertEquals(
                "Exercise not found with id: 99",
                exception.getMessage()
        );

        verify(exerciseRepository, never()).save(any());
    }

    // ---------------------------------------------------------
    // ENABLE / DISABLE EXERCISE
    // ---------------------------------------------------------

    @Test
    void disableExercise_shouldSetActiveFalse() {

        Exercise exercise = new Exercise();
        exercise.setId(1L);
        exercise.setActive(true);

        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        adminService.disableExercise(1L);

        assertFalse(exercise.getActive());

        verify(exerciseRepository).save(exercise);
    }

    @Test
    void enableExercise_shouldSetActiveTrue() {

        Exercise exercise = new Exercise();
        exercise.setId(1L);
        exercise.setActive(false);

        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        adminService.enableExercise(1L);

        assertTrue(exercise.getActive());

        verify(exerciseRepository).save(exercise);
    }

    @Test
    void disableExercise_whenNotFound_shouldThrowException() {

        when(exerciseRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.disableExercise(99L)
        );

        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void enableExercise_whenNotFound_shouldThrowException() {

        when(exerciseRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.enableExercise(99L)
        );

        verify(exerciseRepository, never()).save(any());
    }

    // ---------------------------------------------------------
    // USERS
    // ---------------------------------------------------------

    @Test
    void deleteUser_shouldDeleteUser() {

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        adminService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_whenNotFound_shouldThrowException() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.deleteUser(99L)
        );

        verify(userRepository, never()).delete(any());
    }

    @Test
    void disableUser_shouldDisableUserAndDeleteTokens() {

        User user = new User();
        user.setId(1L);
        user.setEnabled(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        adminService.disableUser(1L);

        assertFalse(user.getEnabled());

        verify(userRepository).save(user);
        verify(refreshTokenService).deleteAllUserTokens(1L);
    }

    @Test
    void enableUser_shouldEnableUser() {

        User user = new User();
        user.setId(1L);
        user.setEnabled(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        adminService.enableUser(1L);

        assertTrue(user.getEnabled());

        verify(userRepository).save(user);
        verifyNoInteractions(refreshTokenService);
    }

    // ---------------------------------------------------------
    // USAGE STATS
    // ---------------------------------------------------------

    @Test
    void getUsageStats_shouldReturnAllStatistics() {

        when(userRepository.count()).thenReturn(7L);
        when(foodLogRepository.count()).thenReturn(14L);
        when(foodLogRepository.countByLogDate(LocalDate.now()))
                .thenReturn(2L);

        when(exerciseRepository.count()).thenReturn(252L);
        when(exerciseRepository.countByActiveTrue()).thenReturn(250L);
        when(exerciseRepository.countByActiveFalse()).thenReturn(2L);

        when(usageTrackingService.getTodayUsage("login"))
                .thenReturn(10L);
        when(usageTrackingService.getTodayUsage("register"))
                .thenReturn(3L);
        when(usageTrackingService.getTodayUsage("nutrition"))
                .thenReturn(5L);
        when(usageTrackingService.getTodayUsage("youtube"))
                .thenReturn(4L);

        AdminUsageStatsResponseDto result =
                adminService.getUsageStats();

        assertEquals(7L, result.getTotalUsers());
        assertEquals(14L, result.getTotalFoodLogs());
        assertEquals(2L, result.getTodayFoodLogs());

        assertEquals(252L, result.getTotalExercises());
        assertEquals(250L, result.getActiveExercises());
        assertEquals(2L, result.getDisabledExercises());

        assertEquals(10L, result.getTodayLoginRequests());
        assertEquals(3L, result.getTodayRegistrationRequests());
        assertEquals(5L, result.getTodayNutritionRequests());
        assertEquals(4L, result.getTodayYoutubeRequests());
    }

    // ---------------------------------------------------------
    // GET ALL USERS
    // ---------------------------------------------------------

    @Test
    void getAllUsers_shouldMapUsersToDtos() {

        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("john");
        user1.setEmail("john@example.com");
        user1.setRole(Role.ROLE_USER);
        user1.setEnabled(true);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("jane");
        user2.setEmail("jane@example.com");
        user2.setRole(Role.ROLE_ADMIN);
        user2.setEnabled(false);

        Page<User> users =
                new PageImpl<>(List.of(user1, user2));

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(users);

        Page<AdminUserSummaryDto> result =
                adminService.getAllUsers(0);

        assertEquals(2, result.getContent().size());

        AdminUserSummaryDto first =
                result.getContent().get(0);

        assertEquals(1L, first.getId());
        assertEquals("john", first.getUsername());
        assertEquals("john@example.com", first.getEmail());
        assertEquals(Role.ROLE_USER, first.getRole());
        assertTrue(first.getEnabled());

        AdminUserSummaryDto second =
                result.getContent().get(1);

        assertFalse(second.getEnabled());

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(userRepository).findAll(captor.capture());

        Pageable pageable = captor.getValue();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
        assertEquals(
                Sort.Direction.ASC,
                pageable.getSort().getOrderFor("username").getDirection()
        );
    }

    @Test
    void getAllUsers_withNegativePage_shouldUseZero() {

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(Page.empty());

        adminService.getAllUsers(-5);

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(userRepository).findAll(captor.capture());

        assertEquals(0, captor.getValue().getPageNumber());
    }

    // ---------------------------------------------------------
    // ADMIN EXERCISE LIST
    // ---------------------------------------------------------

    @Test
    void getAllExercises_shouldReturnMappedPage() {

        Exercise exercise = Exercise.builder()
                .id(1L)
                .name("Bench Press")
                .active(true)
                .build();

        ExerciseSummaryDto dto = new ExerciseSummaryDto();

        when(exerciseRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(exercise)));

        when(exerciseMapper.toSummaryDto(exercise))
                .thenReturn(dto);

        Page<ExerciseSummaryDto> result =
                adminService.getAllExercises(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "name",
                        "asc",
                        0
                );

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));

        verify(exerciseMapper).toSummaryDto(exercise);
    }

    @Test
    void getAllExercises_invalidSortAndDirection_shouldUseDefaults() {

        when(exerciseRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        adminService.getAllExercises(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "invalid",
                "invalid",
                -2
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(exerciseRepository)
                .findAll(any(Specification.class), captor.capture());

        Pageable pageable = captor.getValue();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());

        Sort.Order order =
                pageable.getSort().getOrderFor("name");

        assertNotNull(order);
        assertEquals(Sort.Direction.ASC, order.getDirection());
    }

    @Test
    void getAllExercises_descendingSort_shouldUseDescendingOrder() {

        when(exerciseRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        adminService.getAllExercises(
                null,
                Category.CHEST,
                Muscle.MIDDLE_CHEST,
                Equipment.BARBELL,
                Difficulty.INTERMEDIATE,
                ExerciseType.COMPOUND,
                true,
                "name",
                "desc",
                1
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(exerciseRepository)
                .findAll(any(Specification.class), captor.capture());

        Pageable pageable = captor.getValue();

        assertEquals(1, pageable.getPageNumber());

        Sort.Order order =
                pageable.getSort().getOrderFor("name");

        assertNotNull(order);
        assertEquals(Sort.Direction.DESC, order.getDirection());
    }

    // ---------------------------------------------------------
    // ADMIN GET EXERCISE BY ID
    // ---------------------------------------------------------

    @Test
    void getExerciseById_shouldReturnMappedExercise() {

        Exercise exercise = new Exercise();
        exercise.setId(1L);

        ExerciseDetailDto expected =
                new ExerciseDetailDto();

        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        when(exerciseMapper.toDetailDto(exercise))
                .thenReturn(expected);

        ExerciseDetailDto result =
                adminService.getExerciseById(1L);

        assertSame(expected, result);

        verify(exerciseMapper).toDetailDto(exercise);
    }

    @Test
    void getExerciseById_whenNotFound_shouldThrowException() {

        when(exerciseRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> adminService.getExerciseById(99L)
                );

        assertEquals(
                "Exercise not found",
                exception.getMessage()
        );

        verifyNoInteractions(exerciseMapper);
    }

    // ---------------------------------------------------------
    // CONTACT MESSAGES
    // ---------------------------------------------------------

    @Test
    void getContactMessages_shouldReturnMappedPage() {

        LocalDateTime createdAt = LocalDateTime.now();

        ContactMessage message = new ContactMessage();
        message.setId(1L);
        message.setEmail("user@example.com");
        message.setReason(ContactReason.BUG_REPORT);
        message.setMessage("Bug");
        message.setStatus(ContactMessageStatus.OPEN);
        message.setCreatedAt(createdAt);

        when(contactMessageRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(message)));

        Page<ContactMessageResponseDto> result =
                adminService.getContactMessages(
                        ContactMessageStatus.OPEN,
                        "createdAt",
                        "desc",
                        0
                );

        assertEquals(1, result.getTotalElements());

        ContactMessageResponseDto dto =
                result.getContent().get(0);

        assertEquals(1L, dto.getId());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals(ContactReason.BUG_REPORT, dto.getReason());
        assertEquals("Bug", dto.getMessage());
        assertEquals(ContactMessageStatus.OPEN, dto.getStatus());
        assertEquals(createdAt, dto.getCreatedAt());
    }

    @Test
    void getContactMessages_invalidSortAndDirection_shouldUseDefaults() {

        when(contactMessageRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        adminService.getContactMessages(
                null,
                "invalid",
                "invalid",
                -1
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(contactMessageRepository)
                .findAll(any(Specification.class), captor.capture());

        Pageable pageable = captor.getValue();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());

        Sort.Order order =
                pageable.getSort().getOrderFor("createdAt");

        assertNotNull(order);
        assertEquals(
                Sort.Direction.DESC,
                order.getDirection()
        );
    }

    @Test
    void replyToContactMessage_shouldSendEmail() {

        ContactMessage message = new ContactMessage();
        message.setId(1L);
        message.setEmail("user@example.com");

        when(contactMessageRepository.findById(1L))
                .thenReturn(Optional.of(message));

        adminService.replyToContactMessage(
                1L,
                "Your issue has been resolved."
        );

        verify(emailService).sendEmail(
                "user@example.com",
                "FitSphere Support",
                "Your issue has been resolved."
        );
    }

    @Test
    void replyToContactMessage_whenNotFound_shouldThrowException() {

        when(contactMessageRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.replyToContactMessage(
                        99L,
                        "Reply"
                )
        );

        verifyNoInteractions(emailService);
    }

    @Test
    void updateContactMessageStatus_shouldUpdateAndSave() {

        ContactMessage message = new ContactMessage();
        message.setId(1L);
        message.setStatus(ContactMessageStatus.OPEN);

        when(contactMessageRepository.findById(1L))
                .thenReturn(Optional.of(message));

        adminService.updateContactMessageStatus(
                1L,
                ContactMessageStatus.RESOLVED
        );

        assertEquals(
                ContactMessageStatus.RESOLVED,
                message.getStatus()
        );

        verify(contactMessageRepository).save(message);
    }

    @Test
    void updateContactMessageStatus_whenNotFound_shouldThrowException() {

        when(contactMessageRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.updateContactMessageStatus(
                        99L,
                        ContactMessageStatus.RESOLVED
                )
        );

        verify(contactMessageRepository, never())
                .save(any());
    }

    @Test
    void deleteContactMessage_shouldDeleteById() {

        when(contactMessageRepository.existsById(1L))
                .thenReturn(true);

        adminService.deleteContactMessage(1L);

        verify(contactMessageRepository).deleteById(1L);
    }

    @Test
    void deleteContactMessage_whenNotFound_shouldThrowException() {

        when(contactMessageRepository.existsById(99L))
                .thenReturn(false);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> adminService.deleteContactMessage(99L)
                );

        assertEquals(
                "Contact message not found with id: 99",
                exception.getMessage()
        );

        verify(contactMessageRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void getContactMessageById_shouldReturnDto() {

        LocalDateTime createdAt = LocalDateTime.now();

        ContactMessage message = new ContactMessage();
        message.setId(1L);
        message.setEmail("user@example.com");
        message.setReason(ContactReason.ACCOUNT_ISSUE);
        message.setMessage("Account problem");
        message.setStatus(ContactMessageStatus.IN_PROGRESS);
        message.setCreatedAt(createdAt);

        when(contactMessageRepository.findById(1L))
                .thenReturn(Optional.of(message));

        ContactMessageResponseDto result =
                adminService.getContactMessageById(1L);

        assertEquals(1L, result.getId());
        assertEquals("user@example.com", result.getEmail());
        assertEquals(ContactReason.ACCOUNT_ISSUE, result.getReason());
        assertEquals("Account problem", result.getMessage());
        assertEquals(
                ContactMessageStatus.IN_PROGRESS,
                result.getStatus()
        );
        assertEquals(createdAt, result.getCreatedAt());
    }

    @Test
    void getContactMessageById_whenNotFound_shouldThrowException() {

        when(contactMessageRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.getContactMessageById(99L)
        );
    }
}