package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.dto.workout.*;
import com.sangamesh.Fitsphere.dto.workout.analytics.ExerciseProgressDto;
import com.sangamesh.Fitsphere.dto.workout.analytics.TrainingVolumeAnalyticsDto;
import com.sangamesh.Fitsphere.dto.workout.analytics.TrainingVolumePointDto;
import com.sangamesh.Fitsphere.entity.*;
import com.sangamesh.Fitsphere.enums.WorkoutAnalyticsPeriod;
import com.sangamesh.Fitsphere.exception.BadRequestException;
import com.sangamesh.Fitsphere.exception.ResourceNotFoundException;
import com.sangamesh.Fitsphere.mapper.WorkoutMapper;
import com.sangamesh.Fitsphere.repository.ExerciseRepository;
import com.sangamesh.Fitsphere.repository.WorkoutExerciseRepository;
import com.sangamesh.Fitsphere.repository.WorkoutSessionRepository;
import com.sangamesh.Fitsphere.repository.WorkoutSetRepository;
import com.sangamesh.Fitsphere.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceImplTest {

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @Mock
    private WorkoutExerciseRepository workoutExerciseRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private WorkoutSetRepository workoutSetRepository;

    @Mock
    private WorkoutMapper workoutMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private WorkoutServiceImpl workoutService;


    // =========================================================
    // START WORKOUT
    // =========================================================

    @Test
    void startWorkout_throwsBadRequest_whenNameIsNull() {

        User user = user();

        given(userService.getCurrentUser()).willReturn(user);

        StartWorkoutRequestDto request = new StartWorkoutRequestDto();
        request.setName(null);

        assertThatThrownBy(() -> workoutService.startWorkout(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Workout name is required");

        verify(workoutSessionRepository, never()).save(any());
    }


    @Test
    void startWorkout_throwsBadRequest_whenNameIsBlank() {

        User user = user();

        given(userService.getCurrentUser()).willReturn(user);

        StartWorkoutRequestDto request = new StartWorkoutRequestDto();
        request.setName("   ");

        assertThatThrownBy(() -> workoutService.startWorkout(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Workout name is required");

        verify(workoutSessionRepository, never()).save(any());
    }


    @Test
    void startWorkout_throwsBadRequest_whenActiveWorkoutExists() {

        User user = user();

        WorkoutSession active = workoutSession(1L, user, false);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository
                .findFirstByUserAndCompletedFalseOrderByStartedAtDesc(user))
                .willReturn(Optional.of(active));

        StartWorkoutRequestDto request = new StartWorkoutRequestDto();
        request.setName("Push Day");

        assertThatThrownBy(() -> workoutService.startWorkout(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("You already have an active workout");

        verify(workoutSessionRepository, never()).save(any());
    }


    @Test
    void startWorkout_trimsNameAndSavesWorkout() {

        User user = user();

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository
                .findFirstByUserAndCompletedFalseOrderByStartedAtDesc(user))
                .willReturn(Optional.empty());

        WorkoutSession saved = workoutSession(1L, user, false);
        saved.setName("Push Day");

        given(workoutSessionRepository.save(any(WorkoutSession.class)))
                .willReturn(saved);

        WorkoutSessionResponseDto response =
                WorkoutSessionResponseDto.builder()
                        .id(1L)
                        .name("Push Day")
                        .build();

        given(workoutMapper.toSessionResponseDto(saved))
                .willReturn(response);

        StartWorkoutRequestDto request = new StartWorkoutRequestDto();
        request.setName("   Push Day   ");

        WorkoutSessionResponseDto result =
                workoutService.startWorkout(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Push Day");

        ArgumentCaptor<WorkoutSession> captor =
                ArgumentCaptor.forClass(WorkoutSession.class);

        verify(workoutSessionRepository).save(captor.capture());

        WorkoutSession savedArgument = captor.getValue();

        assertThat(savedArgument.getName()).isEqualTo("Push Day");
        assertThat(savedArgument.getUser()).isSameAs(user);
        assertThat(savedArgument.getStartedAt()).isNotNull();
    }


    // =========================================================
    // ADD EXERCISE
    // =========================================================

    @Test
    void addExercise_throwsNotFound_whenWorkoutDoesNotBelongToUser() {

        User user = user();

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.empty());

        AddWorkoutExerciseRequestDto request =
                new AddWorkoutExerciseRequestDto();

        request.setExerciseId(10L);

        assertThatThrownBy(() ->
                workoutService.addExercise(1L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Workout session not found");

        verify(exerciseRepository, never()).findById(any());
    }


    @Test
    void addExercise_throwsNotFound_whenExerciseDoesNotExist() {

        User user = user();
        WorkoutSession session = workoutSession(1L, user, false);

        given(userService.getCurrentUser()).willReturn(user);
        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));
        given(exerciseRepository.findById(10L))
                .willReturn(Optional.empty());

        AddWorkoutExerciseRequestDto request =
                new AddWorkoutExerciseRequestDto();

        request.setExerciseId(10L);

        assertThatThrownBy(() ->
                workoutService.addExercise(1L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Exercise not found");
    }


    @Test
    void addExercise_throwsBadRequest_whenExerciseIsInactive() {

        User user = user();
        WorkoutSession session = workoutSession(1L, user, false);

        Exercise exercise = exercise(10L, "Bench Press", false);

        given(userService.getCurrentUser()).willReturn(user);
        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));
        given(exerciseRepository.findById(10L))
                .willReturn(Optional.of(exercise));

        AddWorkoutExerciseRequestDto request =
                new AddWorkoutExerciseRequestDto();

        request.setExerciseId(10L);

        assertThatThrownBy(() ->
                workoutService.addExercise(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Exercise is currently unavailable");
    }


    @Test
    void addExercise_throwsBadRequest_whenExerciseAlreadyAdded() {

        User user = user();

        WorkoutSession session = workoutSession(1L, user, false);

        Exercise exercise = exercise(10L, "Bench Press", true);

        WorkoutExercise existing = workoutExercise(
                5L,
                session,
                exercise,
                1
        );

        given(userService.getCurrentUser()).willReturn(user);
        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));
        given(exerciseRepository.findById(10L))
                .willReturn(Optional.of(exercise));
        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(List.of(existing));

        AddWorkoutExerciseRequestDto request =
                new AddWorkoutExerciseRequestDto();

        request.setExerciseId(10L);

        assertThatThrownBy(() ->
                workoutService.addExercise(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Exercise already added to workout");

        verify(workoutExerciseRepository, never()).save(any());
    }


    @Test
    void addExercise_assignsNextExerciseOrder() {

        User user = user();

        WorkoutSession session = workoutSession(1L, user, false);

        Exercise existingExercise =
                exercise(10L, "Bench Press", true);

        Exercise newExercise =
                exercise(20L, "Incline Press", true);

        List<WorkoutExercise> existing = List.of(
                workoutExercise(5L, session, existingExercise, 1),
                workoutExercise(6L, session, exercise(11L, "Fly", true), 2)
        );

        given(userService.getCurrentUser()).willReturn(user);
        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));
        given(exerciseRepository.findById(20L))
                .willReturn(Optional.of(newExercise));
        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(existing);

        WorkoutExercise saved = workoutExercise(
                7L,
                session,
                newExercise,
                3
        );

        given(workoutExerciseRepository.save(any(WorkoutExercise.class)))
                .willReturn(saved);

        WorkoutExerciseResponseDto response =
                WorkoutExerciseResponseDto.builder()
                        .id(7L)
                        .exerciseId(20L)
                        .exerciseName("Incline Press")
                        .exerciseOrder(3)
                        .build();

        given(workoutMapper.toExerciseResponseDto(saved))
                .willReturn(response);

        AddWorkoutExerciseRequestDto request =
                new AddWorkoutExerciseRequestDto();

        request.setExerciseId(20L);

        WorkoutExerciseResponseDto result =
                workoutService.addExercise(1L, request);

        assertThat(result.getExerciseOrder()).isEqualTo(3);

        verify(workoutExerciseRepository).save(
                argThat(we ->
                        we.getExerciseOrder() == 3
                                && we.getTotalVolume() == 0.0
                                && we.getExercise() == newExercise
                                && we.getWorkoutSession() == session
                )
        );
    }


    // =========================================================
    // ADD SET
    // =========================================================

    @Test
    void addSet_throwsNotFound_whenWorkoutExerciseDoesNotExist() {

        User user = user();
        given(userService.getCurrentUser()).willReturn(user);

        given(workoutExerciseRepository
                .findByIdAndWorkoutSession_User_Id(10L, user.getId()))
                .willReturn(Optional.empty());

        AddWorkoutSetRequestDto request = setRequest(60.0, 10);

        assertThatThrownBy(() ->
                workoutService.addSet(1L, 10L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Workout exercise not found");
    }


    @Test
    void addSet_throwsNotFound_whenWorkoutIdDoesNotMatch() {

        User user = user();

        WorkoutSession session =
                workoutSession(99L, user, false);

        WorkoutExercise exercise =
                workoutExercise(10L, session,
                        exercise(1L, "Bench Press", true), 1);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutExerciseRepository
                .findByIdAndWorkoutSession_User_Id(10L, user.getId()))
                .willReturn(Optional.of(exercise));

        AddWorkoutSetRequestDto request = setRequest(60.0, 10);

        assertThatThrownBy(() ->
                workoutService.addSet(1L, 10L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Workout exercise not found");
    }


    @Test
    void addSet_throwsBadRequest_whenWeightIsNull() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise exercise =
                workoutExercise(10L, session,
                        exercise(1L, "Bench Press", true), 1);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutExerciseRepository
                .findByIdAndWorkoutSession_User_Id(10L, user.getId()))
                .willReturn(Optional.of(exercise));

        AddWorkoutSetRequestDto request =
                setRequest(null, 10);

        assertThatThrownBy(() ->
                workoutService.addSet(1L, 10L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Weight is required");
    }


    @Test
    void addSet_throwsBadRequest_whenRepsIsNull() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise exercise =
                workoutExercise(10L, session,
                        exercise(1L, "Bench Press", true), 1);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutExerciseRepository
                .findByIdAndWorkoutSession_User_Id(10L, user.getId()))
                .willReturn(Optional.of(exercise));

        AddWorkoutSetRequestDto request =
                setRequest(60.0, null);

        assertThatThrownBy(() ->
                workoutService.addSet(1L, 10L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Reps are required");
    }


    @Test
    void addSet_throwsBadRequest_whenWeightIsNegative() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise exercise =
                workoutExercise(10L, session,
                        exercise(1L, "Bench Press", true), 1);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutExerciseRepository
                .findByIdAndWorkoutSession_User_Id(10L, user.getId()))
                .willReturn(Optional.of(exercise));

        AddWorkoutSetRequestDto request =
                setRequest(-10.0, 10);

        assertThatThrownBy(() ->
                workoutService.addSet(1L, 10L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Weight cannot be negative");
    }


    @Test
    void addSet_throwsBadRequest_whenRepsAreZero() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise exercise =
                workoutExercise(10L, session,
                        exercise(1L, "Bench Press", true), 1);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutExerciseRepository
                .findByIdAndWorkoutSession_User_Id(10L, user.getId()))
                .willReturn(Optional.of(exercise));

        AddWorkoutSetRequestDto request =
                setRequest(60.0, 0);

        assertThatThrownBy(() ->
                workoutService.addSet(1L, 10L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Reps must be greater than zero");
    }


    @Test
    void addSet_calculatesVolumeAndEstimatedOneRepMax() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise exercise =
                workoutExercise(10L, session,
                        exercise(1L, "Bench Press", true), 1);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutExerciseRepository
                .findByIdAndWorkoutSession_User_Id(10L, user.getId()))
                .willReturn(Optional.of(exercise));

        given(workoutSetRepository
                .findByWorkoutExerciseIdOrderBySetNumberAsc(10L))
                .willReturn(new ArrayList<>());

        WorkoutSet savedSet = new WorkoutSet();

        given(workoutSetRepository.save(any(WorkoutSet.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

//        given(workoutSetRepository
//                .findMaxEstimatedOneRepMaxExcludingWorkout(
//                        anyLong(), anyLong(), anyLong()))
//                .willReturn(Optional.empty());

        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(List.of(exercise));

        WorkoutSetResponseDto response =
                WorkoutSetResponseDto.builder().build();

        given(workoutMapper.toSetResponseDto(any()))
                .willReturn(response);

        AddWorkoutSetRequestDto request =
                setRequest(60.0, 10);

        workoutService.addSet(1L, 10L, request);

        ArgumentCaptor<WorkoutSet> captor =
                ArgumentCaptor.forClass(WorkoutSet.class);

        verify(workoutSetRepository, atLeastOnce())
                .save(captor.capture());

        WorkoutSet result = captor.getAllValues().get(0);

        assertThat(result.getWeight()).isEqualTo(60.0);
        assertThat(result.getReps()).isEqualTo(10);
        assertThat(result.getVolume()).isEqualTo(600.0);
        assertThat(result.getEstimatedOneRepMax()).isEqualTo(80.0);
    }


    @Test
    void addSet_assignsNextSetNumber() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise exercise =
                workoutExercise(10L, session,
                        exercise(1L, "Bench Press", true), 1);

        WorkoutSet existing1 = set(1L, exercise, 1, 50.0, 10);
        WorkoutSet existing2 = set(2L, exercise, 2, 55.0, 8);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutExerciseRepository
                .findByIdAndWorkoutSession_User_Id(10L, user.getId()))
                .willReturn(Optional.of(exercise));

        given(workoutSetRepository
                .findByWorkoutExerciseIdOrderBySetNumberAsc(10L))
                .willReturn(new ArrayList<>(List.of(existing1, existing2)));

        given(workoutSetRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        given(workoutSetRepository
                .findMaxEstimatedOneRepMaxExcludingWorkout(anyLong(), anyLong(), anyLong()))
                .willReturn(Optional.empty());

        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(List.of(exercise));

        given(workoutMapper.toSetResponseDto(any()))
                .willReturn(WorkoutSetResponseDto.builder().build());

        workoutService.addSet(
                1L,
                10L,
                setRequest(60.0, 5)
        );

        verify(workoutSetRepository).save(
                argThat(set ->
                        set.getSetNumber() == 3
                )
        );
    }


    // =========================================================
    // COMPLETE WORKOUT
    // =========================================================

    @Test
    void completeWorkout_throwsNotFound_whenWorkoutDoesNotExist() {

        User user = user();

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                workoutService.completeWorkout(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Workout session not found");
    }


    @Test
    void completeWorkout_throwsBadRequest_whenAlreadyCompleted() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, true);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));

        assertThatThrownBy(() ->
                workoutService.completeWorkout(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Workout already completed");
    }


    @Test
    void completeWorkout_throwsBadRequest_whenWorkoutHasNoExercises() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));

        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(List.of());

        assertThatThrownBy(() ->
                workoutService.completeWorkout(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Cannot complete an empty workout");
    }


    @Test
    void completeWorkout_throwsBadRequest_whenExerciseHasNoSets() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        Exercise exercise =
                exercise(1L, "Bench Press", true);

        WorkoutExercise workoutExercise =
                workoutExercise(10L, session, exercise, 1);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));

        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(List.of(workoutExercise));

        given(workoutSetRepository
                .findByWorkoutExerciseIdOrderBySetNumberAsc(10L))
                .willReturn(List.of());

        assertThatThrownBy(() ->
                workoutService.completeWorkout(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Exercise Bench Press has no sets");
    }


    @Test
    void completeWorkout_calculatesExerciseAndSessionVolume() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        Exercise exercise =
                exercise(1L, "Bench Press", true);

        WorkoutExercise workoutExercise =
                workoutExercise(10L, session, exercise, 1);

        WorkoutSet set1 =
                set(1L, workoutExercise, 1, 60.0, 10);

        set1.setVolume(600.0);
        set1.setEstimatedOneRepMax(80.0);

        WorkoutSet set2 =
                set(2L, workoutExercise, 2, 70.0, 5);

        set2.setVolume(350.0);
        set2.setEstimatedOneRepMax(81.67);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));

        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(List.of(workoutExercise));

        given(workoutSetRepository
                .findByWorkoutExerciseIdOrderBySetNumberAsc(10L))
                .willReturn(new ArrayList<>(List.of(set1, set2)));

        given(workoutSetRepository
                .findMaxEstimatedOneRepMaxExcludingWorkout(
                        1L, user.getId(), 1L))
                .willReturn(Optional.empty());

        given(workoutSessionRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        given(workoutMapper.toSessionResponseDto(any()))
                .willReturn(WorkoutSessionResponseDto.builder().build());

        workoutService.completeWorkout(1L);

        assertThat(workoutExercise.getTotalVolume())
                .isEqualTo(950.0);

        assertThat(session.getTotalVolume())
                .isEqualTo(950.0);

        assertThat(session.getCompleted())
                .isTrue();

        assertThat(session.getCompletedAt())
                .isNotNull();

        verify(workoutExerciseRepository)
                .save(workoutExercise);

        verify(workoutSessionRepository)
                .save(session);
    }


    @Test
    void completeWorkout_marksBestSetAsPersonalRecord_whenNoPreviousBest() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        Exercise exercise =
                exercise(1L, "Bench Press", true);

        WorkoutExercise workoutExercise =
                workoutExercise(10L, session, exercise, 1);

        WorkoutSet set1 =
                set(1L, workoutExercise, 1, 60.0, 10);

        set1.setVolume(600.0);
        set1.setEstimatedOneRepMax(80.0);

        WorkoutSet set2 =
                set(2L, workoutExercise, 2, 60.0, 8);

        set2.setVolume(480.0);
        set2.setEstimatedOneRepMax(76.0);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));

        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(List.of(workoutExercise));

        given(workoutSetRepository
                .findByWorkoutExerciseIdOrderBySetNumberAsc(10L))
                .willReturn(new ArrayList<>(List.of(set1, set2)));

        given(workoutSetRepository
                .findMaxEstimatedOneRepMaxExcludingWorkout(
                        1L, user.getId(), 1L))
                .willReturn(Optional.empty());

        given(workoutSessionRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        given(workoutMapper.toSessionResponseDto(any()))
                .willReturn(WorkoutSessionResponseDto.builder().build());

        workoutService.completeWorkout(1L);

        assertThat(set1.getPersonalRecord()).isTrue();
        assertThat(set2.getPersonalRecord()).isFalse();
    }


    @Test
    void completeWorkout_doesNotMarkPR_whenPreviousBestIsHigher() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        Exercise exercise =
                exercise(1L, "Bench Press", true);

        WorkoutExercise workoutExercise =
                workoutExercise(10L, session, exercise, 1);

        WorkoutSet set =
                set(1L, workoutExercise, 1, 50.0, 5);

        set.setVolume(250.0);
        set.setEstimatedOneRepMax(58.33);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));

        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(List.of(workoutExercise));

        given(workoutSetRepository
                .findByWorkoutExerciseIdOrderBySetNumberAsc(10L))
                .willReturn(new ArrayList<>(List.of(set)));

        given(workoutSetRepository
                .findMaxEstimatedOneRepMaxExcludingWorkout(
                        1L, user.getId(), 1L))
                .willReturn(Optional.of(100.0));

        given(workoutSessionRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        given(workoutMapper.toSessionResponseDto(any()))
                .willReturn(WorkoutSessionResponseDto.builder().build());

        workoutService.completeWorkout(1L);

        assertThat(set.getPersonalRecord()).isFalse();
    }


    // =========================================================
    // REORDER EXERCISES
    // =========================================================

    @Test
    void reorderExercises_throwsBadRequest_whenSizeDoesNotMatch() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise e1 =
                workoutExercise(10L, session,
                        exercise(1L, "Bench", true), 1);

        WorkoutExercise e2 =
                workoutExercise(20L, session,
                        exercise(2L, "Fly", true), 2);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));

        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(List.of(e1, e2));

        assertThatThrownBy(() ->
                workoutService.reorderExercises(
                        1L,
                        List.of(10L)
                ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Invalid exercise order");
    }


    @Test
    void reorderExercises_throwsBadRequest_whenIdsDoNotMatchWorkout() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise e1 =
                workoutExercise(10L, session,
                        exercise(1L, "Bench", true), 1);

        WorkoutExercise e2 =
                workoutExercise(20L, session,
                        exercise(2L, "Fly", true), 2);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));

        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(List.of(e1, e2));

        assertThatThrownBy(() ->
                workoutService.reorderExercises(
                        1L,
                        List.of(10L, 30L)
                ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(
                        "Exercise order does not match workout exercises"
                );
    }


    @Test
    void reorderExercises_throwsBadRequest_whenDuplicateIdsProvided() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise e1 =
                workoutExercise(10L, session,
                        exercise(1L, "Bench", true), 1);

        WorkoutExercise e2 =
                workoutExercise(20L, session,
                        exercise(2L, "Fly", true), 2);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));

        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(List.of(e1, e2));

        assertThatThrownBy(() ->
                workoutService.reorderExercises(
                        1L,
                        List.of(10L, 10L)
                ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(
                        "Exercise order does not match workout exercises"
                );
    }


    @Test
    void reorderExercises_updatesExerciseOrder() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise e1 =
                workoutExercise(10L, session,
                        exercise(1L, "Bench", true), 1);

        WorkoutExercise e2 =
                workoutExercise(20L, session,
                        exercise(2L, "Fly", true), 2);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));

        List<WorkoutExercise> exercises =
                new ArrayList<>(List.of(e1, e2));

        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(exercises);

        workoutService.reorderExercises(
                1L,
                List.of(20L, 10L)
        );

        assertThat(e2.getExerciseOrder()).isEqualTo(1);
        assertThat(e1.getExerciseOrder()).isEqualTo(2);

        verify(workoutExerciseRepository)
                .saveAll(exercises);
    }


    // =========================================================
    // GETTERS
    // =========================================================

    @Test
    void getActiveWorkout_throwsNotFound_whenNoActiveWorkoutExists() {

        User user = user();

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository
                .findFirstByUserAndCompletedFalseOrderByStartedAtDesc(user))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                workoutService.getActiveWorkout())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No active workout found");
    }


    @Test
    void getActiveWorkout_returnsActiveWorkout() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutSessionResponseDto response =
                WorkoutSessionResponseDto.builder()
                        .id(1L)
                        .name("Push Day")
                        .completed(false)
                        .build();

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository
                .findFirstByUserAndCompletedFalseOrderByStartedAtDesc(user))
                .willReturn(Optional.of(session));

        given(workoutMapper.toSessionResponseDto(session))
                .willReturn(response);

        WorkoutSessionResponseDto result =
                workoutService.getActiveWorkout();

        assertThat(result).isSameAs(response);
    }


    @Test
    void getWorkoutById_throwsNotFound_whenWorkoutDoesNotExist() {

        User user = user();

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository
                .findByIdAndUser(1L, user))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                workoutService.getWorkoutById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Workout session not found");
    }


    @Test
    void getWorkoutHistory_returnsMappedCompletedWorkouts() {

        User user = user();

        WorkoutSession first =
                workoutSession(1L, user, true);

        WorkoutSession second =
                workoutSession(2L, user, true);

        WorkoutSessionResponseDto firstDto =
                WorkoutSessionResponseDto.builder()
                        .id(1L)
                        .build();

        WorkoutSessionResponseDto secondDto =
                WorkoutSessionResponseDto.builder()
                        .id(2L)
                        .build();

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository
                .findByUserAndCompletedTrueOrderByStartedAtDesc(user))
                .willReturn(List.of(first, second));

        given(workoutMapper.toSessionResponseDto(first))
                .willReturn(firstDto);

        given(workoutMapper.toSessionResponseDto(second))
                .willReturn(secondDto);

        List<WorkoutSessionResponseDto> result =
                workoutService.getWorkoutHistory();

        assertThat(result)
                .containsExactly(firstDto, secondDto);
    }


    // =========================================================
    // UPDATE SET
    // =========================================================

    @Test
    void updateSet_throwsNotFound_whenSetDoesNotExist() {

        User user = user();

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSetRepository
                .findByIdAndWorkoutExercise_WorkoutSession_User_Id(
                        100L, user.getId()))
                .willReturn(Optional.empty());

        UpdateWorkoutSetRequestDto request =
                new UpdateWorkoutSetRequestDto();

        request.setWeight(60.0);

        assertThatThrownBy(() ->
                workoutService.updateSet(
                        1L, 10L, 100L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Workout set not found");
    }


    @Test
    void updateSet_throwsNotFound_whenIdsDoNotMatch() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise exercise =
                workoutExercise(10L, session,
                        exercise(1L, "Bench", true), 1);

        WorkoutSet set =
                set(100L, exercise, 1, 50.0, 10);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSetRepository
                .findByIdAndWorkoutExercise_WorkoutSession_User_Id(
                        100L, user.getId()))
                .willReturn(Optional.of(set));

        UpdateWorkoutSetRequestDto request =
                new UpdateWorkoutSetRequestDto();

        request.setWeight(60.0);

        assertThatThrownBy(() ->
                workoutService.updateSet(
                        1L, 999L, 100L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Workout set not found");
    }


    @Test
    void updateSet_throwsBadRequest_whenNoFieldsProvided() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise exercise =
                workoutExercise(10L, session,
                        exercise(1L, "Bench", true), 1);

        WorkoutSet set =
                set(100L, exercise, 1, 50.0, 10);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSetRepository
                .findByIdAndWorkoutExercise_WorkoutSession_User_Id(
                        100L, user.getId()))
                .willReturn(Optional.of(set));

        UpdateWorkoutSetRequestDto request =
                new UpdateWorkoutSetRequestDto();

        assertThatThrownBy(() ->
                workoutService.updateSet(
                        1L, 10L, 100L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("At least one field is required");
    }


    @Test
    void updateSet_throwsBadRequest_whenWeightNegative() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise exercise =
                workoutExercise(10L, session,
                        exercise(1L, "Bench", true), 1);

        WorkoutSet set =
                set(100L, exercise, 1, 50.0, 10);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSetRepository
                .findByIdAndWorkoutExercise_WorkoutSession_User_Id(
                        100L, user.getId()))
                .willReturn(Optional.of(set));

        UpdateWorkoutSetRequestDto request =
                new UpdateWorkoutSetRequestDto();

        request.setWeight(-1.0);

        assertThatThrownBy(() ->
                workoutService.updateSet(
                        1L, 10L, 100L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Weight cannot be negative");
    }


    @Test
    void updateSet_throwsBadRequest_whenRepsAreZero() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise exercise =
                workoutExercise(10L, session,
                        exercise(1L, "Bench", true), 1);

        WorkoutSet set =
                set(100L, exercise, 1, 50.0, 10);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSetRepository
                .findByIdAndWorkoutExercise_WorkoutSession_User_Id(
                        100L, user.getId()))
                .willReturn(Optional.of(set));

        UpdateWorkoutSetRequestDto request =
                new UpdateWorkoutSetRequestDto();

        request.setReps(0);

        assertThatThrownBy(() ->
                workoutService.updateSet(
                        1L, 10L, 100L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Reps must be greater than zero");
    }


    @Test
    void updateSet_recalculatesVolumeAndEstimatedOneRepMax() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise exercise =
                workoutExercise(10L, session,
                        exercise(1L, "Bench", true), 1);

        WorkoutSet set =
                set(100L, exercise, 1, 50.0, 5);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSetRepository
                .findByIdAndWorkoutExercise_WorkoutSession_User_Id(
                        100L, user.getId()))
                .willReturn(Optional.of(set));

        given(workoutSetRepository
                .findByWorkoutExerciseIdOrderBySetNumberAsc(10L))
                .willReturn(new ArrayList<>(List.of(set)));

        given(workoutSetRepository
                .findMaxEstimatedOneRepMaxExcludingWorkout(
                        anyLong(), anyLong(), anyLong()))
                .willReturn(Optional.empty());

        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(List.of(exercise));

        given(workoutSetRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        given(workoutMapper.toSetResponseDto(any()))
                .willReturn(WorkoutSetResponseDto.builder().build());

        UpdateWorkoutSetRequestDto request =
                new UpdateWorkoutSetRequestDto();

        request.setWeight(60.0);
        request.setReps(10);

        workoutService.updateSet(
                1L, 10L, 100L, request);

        assertThat(set.getWeight()).isEqualTo(60.0);
        assertThat(set.getReps()).isEqualTo(10);
        assertThat(set.getVolume()).isEqualTo(600.0);
        assertThat(set.getEstimatedOneRepMax()).isEqualTo(80.0);
    }


    // =========================================================
    // DELETE SET
    // =========================================================

    @Test
    void deleteSet_throwsNotFound_whenSetDoesNotExist() {

        User user = user();

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSetRepository
                .findByIdAndWorkoutExercise_WorkoutSession_User_Id(
                        100L, user.getId()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                workoutService.deleteSet(1L, 10L, 100L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Workout set not found");
    }


    @Test
    void deleteSet_renumbersRemainingSets() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise exercise =
                workoutExercise(10L, session,
                        exercise(1L, "Bench", true), 1);

        WorkoutSet deleted =
                set(100L, exercise, 1, 50.0, 10);

        WorkoutSet remaining =
                set(101L, exercise, 3, 60.0, 8);

        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(List.of(exercise));

        given(workoutSetRepository
                .findByIdAndWorkoutExercise_WorkoutSession_User_Id(
                        100L, user.getId()))
                .willReturn(Optional.of(deleted));

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSetRepository
                .findByWorkoutExerciseIdOrderBySetNumberAsc(10L))
                .willReturn(new ArrayList<>(List.of(remaining)));

        given(workoutSetRepository
                .findMaxEstimatedOneRepMaxExcludingWorkout(
                        anyLong(), anyLong(), anyLong()))
                .willReturn(Optional.empty());

        workoutService.deleteSet(1L, 10L, 100L);

        assertThat(remaining.getSetNumber()).isEqualTo(1);

        verify(workoutSetRepository).delete(deleted);
        verify(workoutSetRepository).flush();
        verify(workoutSetRepository,times(2)).saveAll(anyList());
    }


    // =========================================================
    // REMOVE EXERCISE
    // =========================================================

    @Test
    void removeExercise_throwsNotFound_whenExerciseDoesNotExist() {

        User user = user();

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutExerciseRepository
                .findByIdAndWorkoutSession_User_Id(10L, user.getId()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                workoutService.removeExercise(1L, 10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Workout exercise not found");
    }


    @Test
    void removeExercise_throwsNotFound_whenWorkoutIdDoesNotMatch() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise exercise =
                workoutExercise(10L, session,
                        exercise(1L, "Bench", true), 1);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutExerciseRepository
                .findByIdAndWorkoutSession_User_Id(10L, user.getId()))
                .willReturn(Optional.of(exercise));

        assertThatThrownBy(() ->
                workoutService.removeExercise(999L, 10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Workout exercise not found");
    }


    @Test
    void removeExercise_deletesAndRenumbersExercises() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        WorkoutExercise removed =
                workoutExercise(10L, session,
                        exercise(1L, "Bench", true), 1);

        WorkoutExercise remaining =
                workoutExercise(20L, session,
                        exercise(2L, "Fly", true), 2);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutExerciseRepository
                .findByIdAndWorkoutSession_User_Id(10L, user.getId()))
                .willReturn(Optional.of(removed));

        given(workoutExerciseRepository
                .findByWorkoutSessionIdOrderByExerciseOrderAsc(1L))
                .willReturn(new ArrayList<>(List.of(remaining)));

        given(workoutSetRepository
                .findByWorkoutExerciseIdOrderBySetNumberAsc(20L))
                .willReturn(List.of());

        workoutService.removeExercise(1L, 10L);

        assertThat(remaining.getExerciseOrder())
                .isEqualTo(1);

        verify(workoutExerciseRepository).delete(removed);
        verify(workoutExerciseRepository).flush();
        verify(workoutExerciseRepository,times(2)).saveAll(anyList());
    }


    // =========================================================
    // DELETE WORKOUT
    // =========================================================

    @Test
    void deleteWorkout_throwsNotFound_whenWorkoutDoesNotExist() {

        User user = user();

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                workoutService.deleteWorkout(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Workout not found");
    }


    @Test
    void deleteWorkout_deletesWorkout() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));

        workoutService.deleteWorkout(1L);

        verify(workoutSessionRepository)
                .delete(session);
    }


    // =========================================================
    // RENAME
    // =========================================================

    @Test
    void renameWorkout_throwsBadRequest_whenNameIsBlank() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));

        RenameWorkoutRequestDto request =
                new RenameWorkoutRequestDto();

        request.setName("   ");

        assertThatThrownBy(() ->
                workoutService.renameWorkout(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Workout name is required");
    }


    @Test
    void renameWorkout_trimsAndSavesName() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, false);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository.findByIdAndUser(1L, user))
                .willReturn(Optional.of(session));

        given(workoutSessionRepository.save(session))
                .willReturn(session);

        given(workoutMapper.toSessionResponseDto(session))
                .willReturn(
                        WorkoutSessionResponseDto.builder()
                                .name("Leg Day")
                                .build()
                );

        RenameWorkoutRequestDto request =
                new RenameWorkoutRequestDto();

        request.setName("  Leg Day  ");

        WorkoutSessionResponseDto result =
                workoutService.renameWorkout(1L, request);

        assertThat(session.getName()).isEqualTo("Leg Day");
        assertThat(result.getName()).isEqualTo("Leg Day");

        verify(workoutSessionRepository).save(session);
    }


    // =========================================================
    // EXERCISE PROGRESS
    // =========================================================

    @Test
    void getExerciseProgress_throwsNotFound_whenExerciseDoesNotExist() {

        User user = user();

        given(userService.getCurrentUser()).willReturn(user);

        given(exerciseRepository.findById(1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                workoutService.getExerciseProgress(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Exercise not found");
    }


    @Test
    void getExerciseProgress_calculatesBestOneRepMaxAndPRCount() {

        User user = user();

        Exercise exercise =
                exercise(1L, "Bench Press", true);

        WorkoutSession session1 =
                workoutSession(10L, user, true);

        session1.setName("Push Day");

        WorkoutSession session2 =
                workoutSession(20L, user, true);

        session2.setName("Push Day 2");

        WorkoutExercise we1 =
                workoutExercise(100L, session1, exercise, 1);

        WorkoutExercise we2 =
                workoutExercise(200L, session2, exercise, 1);

        WorkoutSet set1 =
                set(1L, we1, 1, 60.0, 10);

        set1.setVolume(600.0);
        set1.setEstimatedOneRepMax(80.0);
        set1.setPersonalRecord(true);

        WorkoutSet set2 =
                set(2L, we2, 1, 70.0, 10);

        set2.setVolume(700.0);
        set2.setEstimatedOneRepMax(93.33);
        set2.setPersonalRecord(true);

        given(userService.getCurrentUser()).willReturn(user);

        given(exerciseRepository.findById(1L))
                .willReturn(Optional.of(exercise));

        given(workoutExerciseRepository
                .findByWorkoutSession_User_IdAndExercise_IdAndWorkoutSession_CompletedTrueOrderByWorkoutSession_StartedAtAsc(
                        user.getId(), 1L))
                .willReturn(List.of(we1, we2));

        given(workoutSetRepository
                .findByWorkoutExerciseIdOrderBySetNumberAsc(100L))
                .willReturn(List.of(set1));

        given(workoutSetRepository
                .findByWorkoutExerciseIdOrderBySetNumberAsc(200L))
                .willReturn(List.of(set2));

        ExerciseProgressDto result =
                workoutService.getExerciseProgress(1L);

        assertThat(result.getExerciseId()).isEqualTo(1L);
        assertThat(result.getExerciseName())
                .isEqualTo("Bench Press");

        assertThat(result.getCurrentBestEstimatedOneRepMax())
                .isEqualTo(93.33);

        assertThat(result.getTotalPersonalRecords())
                .isEqualTo(2);

        assertThat(result.getHistory())
                .hasSize(2);
    }


    // =========================================================
    // TRAINING VOLUME ANALYTICS
    // =========================================================

    @Test
    void getTrainingVolumeAnalytics_weekly_returnsEightBuckets() {

        User user = user();

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository
                .findByUserAndCompletedTrueAndStartedAtBetweenOrderByStartedAtAsc(
                        eq(user),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)))
                .willReturn(List.of());

        TrainingVolumeAnalyticsDto result =
                workoutService.getTrainingVolumeAnalytics(
                        WorkoutAnalyticsPeriod.WEEKLY
                );

        assertThat(result.getPeriod())
                .isEqualTo(WorkoutAnalyticsPeriod.WEEKLY);

        assertThat(result.getHistory())
                .hasSize(8);

        assertThat(result.getCurrentPeriodVolume())
                .isEqualTo(0.0);

        assertThat(result.getPreviousPeriodVolume())
                .isEqualTo(0.0);

        assertThat(result.getVolumeChangePercentage())
                .isEqualTo(0.0);
    }


    @Test
    void getTrainingVolumeAnalytics_monthly_returnsSixBuckets() {

        User user = user();

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository
                .findByUserAndCompletedTrueAndStartedAtBetweenOrderByStartedAtAsc(
                        eq(user),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)))
                .willReturn(List.of());

        TrainingVolumeAnalyticsDto result =
                workoutService.getTrainingVolumeAnalytics(
                        WorkoutAnalyticsPeriod.MONTHLY
                );

        assertThat(result.getPeriod())
                .isEqualTo(WorkoutAnalyticsPeriod.MONTHLY);

        assertThat(result.getHistory())
                .hasSize(6);

        assertThat(result.getCurrentPeriodVolume())
                .isEqualTo(0.0);

        assertThat(result.getPreviousPeriodVolume())
                .isEqualTo(0.0);
    }


    @Test
    void getTrainingVolumeAnalytics_calculatesCurrentPreviousAndPercentage() {

        User user = user();

        given(workoutSessionRepository
                .findByUserAndCompletedTrueAndStartedAtBetweenOrderByStartedAtAsc(
                        eq(user),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)))
                .willReturn(List.of());

        given(userService.getCurrentUser())
                .willReturn(user);

        TrainingVolumeAnalyticsDto result =
                workoutService.getTrainingVolumeAnalytics(
                        WorkoutAnalyticsPeriod.WEEKLY
                );

        assertThat(result.getCurrentPeriodVolume())
                .isEqualTo(0.0);

        assertThat(result.getPreviousPeriodVolume())
                .isEqualTo(0.0);

        assertThat(result.getVolumeChangePercentage())
                .isEqualTo(0.0);
    }


    @Test
    void getTrainingVolumeAnalytics_includesEmptyWeeksAsZeroVolume() {

        User user = user();

        WorkoutSession session =
                workoutSession(1L, user, true);

        session.setStartedAt(
                LocalDateTime.now().minusWeeks(1)
        );

        session.setTotalVolume(500.0);

        given(userService.getCurrentUser()).willReturn(user);

        given(workoutSessionRepository
                .findByUserAndCompletedTrueAndStartedAtBetweenOrderByStartedAtAsc(
                        eq(user),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)))
                .willReturn(List.of(session));

        TrainingVolumeAnalyticsDto result =
                workoutService.getTrainingVolumeAnalytics(
                        WorkoutAnalyticsPeriod.WEEKLY
                );

        assertThat(result.getHistory())
                .hasSize(8);

        assertThat(result.getHistory())
                .extracting(TrainingVolumePointDto::getTotalVolume)
                .contains(0.0);
    }


    // =========================================================
    // TEST DATA HELPERS
    // =========================================================

    private User user() {

        User user = new User();

        user.setId(100L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");

        return user;
    }


    private WorkoutSession workoutSession(
            Long id,
            User user,
            boolean completed
    ) {

        WorkoutSession session =
                new WorkoutSession();

        session.setId(id);
        session.setUser(user);
        session.setName("Test Workout");
        session.setStartedAt(LocalDateTime.now());
        session.setCompleted(completed);
        session.setTotalVolume(0.0);

        return session;
    }


    private Exercise exercise(
            Long id,
            String name,
            boolean active
    ) {

        Exercise exercise =
                new Exercise();

        exercise.setId(id);
        exercise.setName(name);
        exercise.setActive(active);

        return exercise;
    }


    private WorkoutExercise workoutExercise(
            Long id,
            WorkoutSession session,
            Exercise exercise,
            int order
    ) {

        WorkoutExercise workoutExercise =
                new WorkoutExercise();

        workoutExercise.setId(id);
        workoutExercise.setWorkoutSession(session);
        workoutExercise.setExercise(exercise);
        workoutExercise.setExerciseOrder(order);
        workoutExercise.setTotalVolume(0.0);

        return workoutExercise;
    }


    private WorkoutSet set(
            Long id,
            WorkoutExercise exercise,
            int setNumber,
            double weight,
            int reps
    ) {

        WorkoutSet set = new WorkoutSet();

        set.setId(id);
        set.setWorkoutExercise(exercise);
        set.setSetNumber(setNumber);
        set.setWeight(weight);
        set.setReps(reps);
        set.setVolume(weight * reps);
        set.setEstimatedOneRepMax(
                weight * (1 + reps / 30.0)
        );
        set.setPersonalRecord(false);

        return set;
    }


    private AddWorkoutSetRequestDto setRequest(
            Double weight,
            Integer reps
    ) {

        AddWorkoutSetRequestDto request =
                new AddWorkoutSetRequestDto();

        request.setWeight(weight);
        request.setReps(reps);
        return request;
    }
}