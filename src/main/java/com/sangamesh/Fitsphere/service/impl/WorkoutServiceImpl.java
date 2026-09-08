package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.dto.workout.*;
import com.sangamesh.Fitsphere.dto.workout.analytics.ExerciseProgressDto;
import com.sangamesh.Fitsphere.dto.workout.analytics.ExerciseProgressPointDto;
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
import com.sangamesh.Fitsphere.service.WorkoutService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutMapper workoutMapper;
    private final UserService userService;

    @Override
    public WorkoutSessionResponseDto startWorkout(StartWorkoutRequestDto request) {
        User user = userService.getCurrentUser();
        if (request.getName() == null
                || request.getName().isBlank()) {
            throw new BadRequestException(
                    "Workout name is required");
        }
        workoutSessionRepository
                .findFirstByUserAndCompletedFalseOrderByStartedAtDesc(user)
                .ifPresent(session -> {
                    throw new BadRequestException(
                            "You already have an active workout");
                });

        WorkoutSession workoutSession = new WorkoutSession();
        workoutSession.setUser(user);
        workoutSession.setName(request.getName().trim());
        workoutSession.setStartedAt(LocalDateTime.now());
        WorkoutSession savedSession = workoutSessionRepository.save(workoutSession);
        return workoutMapper.toSessionResponseDto(savedSession);
    }

    private WorkoutSessionResponseDto toSessionResponseDto(WorkoutSession session) {

        return WorkoutSessionResponseDto.builder()
                .id(session.getId())
                .name(session.getName())
                .startedAt(session.getStartedAt())
                .completedAt(session.getCompletedAt())
                .completed(session.getCompleted())
                .totalVolume(session.getTotalVolume())
                .exercises(List.of())
                .build();
    }

    @Override
    public WorkoutExerciseResponseDto addExercise(Long workoutId, AddWorkoutExerciseRequestDto request) {
        User user = userService.getCurrentUser();
        WorkoutSession session = workoutSessionRepository
                .findByIdAndUser(workoutId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workout session not found"));

//        if (session.getCompleted()) {
//            throw new BadRequestException(
//                    "Completed workout cannot be modified");
//        }

        Exercise exercise = exerciseRepository
                .findById(request.getExerciseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exercise not found"));

        if (!exercise.getActive()) {
            throw new BadRequestException(
                    "Exercise is currently unavailable");
        }

        List<WorkoutExercise> existingExercises = workoutExerciseRepository
                        .findByWorkoutSessionIdOrderByExerciseOrderAsc(
                                session.getId());

        boolean alreadyAdded = existingExercises.stream()
                .anyMatch(workoutExercise ->
                        workoutExercise.getExercise()
                                .getId()
                                .equals(exercise.getId()));

        if (alreadyAdded) {
            throw new BadRequestException(
                    "Exercise already added to workout");
        }

        WorkoutExercise workoutExercise = new WorkoutExercise();

        workoutExercise.setWorkoutSession(session);
        workoutExercise.setExercise(exercise);
        workoutExercise.setExerciseOrder(
                existingExercises.size() + 1);
        workoutExercise.setTotalVolume(0.0);

        WorkoutExercise savedExercise = workoutExerciseRepository.save(workoutExercise);

        return workoutMapper.toExerciseResponseDto(savedExercise);
    }

    @Override
    public WorkoutSetResponseDto addSet(Long workoutId, Long workoutExerciseId, AddWorkoutSetRequestDto request) {
        User user = userService.getCurrentUser();
        WorkoutExercise workoutExercise = workoutExerciseRepository
                .findByIdAndWorkoutSession_User_Id(workoutExerciseId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Workout exercise not found"));

        if (!workoutExercise.getWorkoutSession().getId().equals(workoutId)) {
            throw new ResourceNotFoundException("Workout exercise not found");
        }

//        if (workoutExercise.getWorkoutSession().getCompleted()) {
//            throw new BadRequestException("Completed workout cannot be modified");
//        }

        validateSet(request);

        List<WorkoutSet> existingSets = workoutSetRepository
                .findByWorkoutExerciseIdOrderBySetNumberAsc(workoutExerciseId);

        WorkoutSet workoutSet = new WorkoutSet();
        workoutSet.setWorkoutExercise(workoutExercise);
        workoutSet.setSetNumber(existingSets.size() + 1);
        workoutSet.setWeight(request.getWeight());
        workoutSet.setReps(request.getReps());

        double volume = request.getWeight() * request.getReps();
        double estimatedOneRepMax = request.getWeight() * (1 + request.getReps() / 30.0);

        workoutSet.setVolume(round(volume));
        workoutSet.setEstimatedOneRepMax(round(estimatedOneRepMax));
        workoutSet.setPersonalRecord(false);
        recalculateSet(workoutSet);
        WorkoutSet savedSet = workoutSetRepository.save(workoutSet);
        recalculatePersonalRecords(workoutExercise);
        recalculateWorkoutTotals(workoutExercise.getWorkoutSession());
        return workoutMapper.toSetResponseDto(savedSet);
    }

    @Override
    @Transactional
    public WorkoutSessionResponseDto completeWorkout(Long workoutId) {
        User user = userService.getCurrentUser();
        WorkoutSession session = workoutSessionRepository
                .findByIdAndUser(workoutId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workout session not found"));
        if (session.getCompleted()) {
            throw new BadRequestException("Workout already completed");
        }
        List<WorkoutExercise> workoutExercises = workoutExerciseRepository
                        .findByWorkoutSessionIdOrderByExerciseOrderAsc(
                                session.getId());

        if (workoutExercises.isEmpty()) {
            throw new BadRequestException("Cannot complete an empty workout");
        }

        double sessionTotalVolume = 0.0;
        for (WorkoutExercise workoutExercise : workoutExercises) {
            List<WorkoutSet> sets = workoutSetRepository
                            .findByWorkoutExerciseIdOrderBySetNumberAsc(
                                    workoutExercise.getId());

            if (sets.isEmpty()) {
                throw new BadRequestException("Exercise " + workoutExercise.getExercise().getName() + " has no sets");
            }

            double exerciseTotalVolume = sets.stream()
                    .map(WorkoutSet::getVolume)
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .sum();

            recalculatePersonalRecords(workoutExercise);

            workoutExercise.setTotalVolume(round(exerciseTotalVolume));
            workoutExerciseRepository.save(workoutExercise);
            sessionTotalVolume += exerciseTotalVolume;
        }
        session.setTotalVolume(round(sessionTotalVolume));
        session.setCompleted(true);

        session.setCompletedAt(LocalDateTime.now());
        WorkoutSession completedSession = workoutSessionRepository.save(session);

        return workoutMapper.toSessionResponseDto(completedSession);
    }

    @Override
    @Transactional
    public void reorderExercises(Long workoutId, List<Long> exerciseIds) {
        User user = userService.getCurrentUser();
        WorkoutSession session = workoutSessionRepository.findByIdAndUser(workoutId, user)
                        .orElseThrow(() -> new ResourceNotFoundException("Workout session not found"));

        List<WorkoutExercise> exercises = workoutExerciseRepository
                        .findByWorkoutSessionIdOrderByExerciseOrderAsc(session.getId());

        if (exerciseIds == null || exerciseIds.size() != exercises.size()) {
            throw new BadRequestException("Invalid exercise order");
        }
        Map<Long, WorkoutExercise> exerciseMap =
                exercises.stream()
                        .collect(Collectors.toMap(WorkoutExercise::getId,
                                exercise -> exercise));

        Set<Long> requestedIds = new HashSet<>(exerciseIds);

        if (requestedIds.size() != exerciseIds.size() || !requestedIds.equals(exerciseMap.keySet())) {
            throw new BadRequestException("Exercise order does not match workout exercises");
        }
        for (int i = 0; i < exerciseIds.size(); i++) {
            WorkoutExercise exercise = exerciseMap.get(exerciseIds.get(i));
            exercise.setExerciseOrder(i + 1);
        }
        workoutExerciseRepository.saveAll(exercises);
    }

    @Override
    public List<WorkoutSessionResponseDto> getWorkoutHistory() {
        User user = userService.getCurrentUser();
        return workoutSessionRepository
                .findByUserAndCompletedTrueOrderByStartedAtDesc(user)
                .stream()
                .map(workoutMapper::toSessionResponseDto)
                .toList();
    }
    @Override
    @Transactional
    public void deleteWorkout(Long workoutId) {

        User user = userService.getCurrentUser();

        WorkoutSession workout = workoutSessionRepository
                .findByIdAndUser(workoutId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workout not found"));

        workoutSessionRepository.delete(workout);
    }

    @Override
    public WorkoutSessionResponseDto getWorkoutById(Long workoutId) {
        User user = userService.getCurrentUser();
        WorkoutSession session = workoutSessionRepository
                .findByIdAndUser(workoutId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workout session not found"));
        return workoutMapper.toSessionResponseDto(session);
    }

    @Override
    public WorkoutSessionResponseDto getActiveWorkout() {
        User user = userService.getCurrentUser();
        WorkoutSession session = workoutSessionRepository
                .findFirstByUserAndCompletedFalseOrderByStartedAtDesc(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No active workout found"));
        return workoutMapper.toSessionResponseDto(session);
    }


    @Override
    public WorkoutSetResponseDto updateSet(Long workoutId, Long workoutExerciseId, Long setId, UpdateWorkoutSetRequestDto request) {
        User user = userService.getCurrentUser();
        WorkoutSet workoutSet = workoutSetRepository
                .findByIdAndWorkoutExercise_WorkoutSession_User_Id(setId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Workout set not found"));

        WorkoutExercise workoutExercise = workoutSet.getWorkoutExercise();

        if (!workoutExercise.getId().equals(workoutExerciseId)
                || !workoutExercise.getWorkoutSession().getId().equals(workoutId)) {
            throw new ResourceNotFoundException("Workout set not found");
        }

        if (request.getWeight() == null && request.getReps() == null) {
            throw new BadRequestException("At least one field is required");
        }

        if (request.getWeight() != null) {
            if (request.getWeight() < 0) {
                throw new BadRequestException("Weight cannot be negative");
            }
            workoutSet.setWeight(request.getWeight());
        }

        if (request.getReps() != null) {
            if (request.getReps() <= 0) {
                throw new BadRequestException("Reps must be greater than zero");
            }
            workoutSet.setReps(request.getReps());
        }

        recalculateSet(workoutSet);
        recalculatePersonalRecords(workoutExercise);
        WorkoutSet updatedSet = workoutSetRepository.save(workoutSet);
        recalculateWorkoutTotals(workoutExercise.getWorkoutSession());
        return workoutMapper.toSetResponseDto(updatedSet);
    }


    @Override
    @Transactional
    public void deleteSet(Long workoutId, Long workoutExerciseId, Long setId) {
        User user = userService.getCurrentUser();
        WorkoutSet workoutSet = workoutSetRepository
                .findByIdAndWorkoutExercise_WorkoutSession_User_Id(setId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Workout set not found"));

        WorkoutExercise workoutExercise = workoutSet.getWorkoutExercise();

        if (!workoutExercise.getId().equals(workoutExerciseId)
                || !workoutExercise.getWorkoutSession().getId().equals(workoutId)) {
            throw new ResourceNotFoundException("Workout set not found");
        }

//        if (workoutExercise.getWorkoutSession().getCompleted()) {
//            throw new BadRequestException("Completed workout cannot be modified");
//        }

        workoutSetRepository.delete(workoutSet);
        workoutSetRepository.flush();
        renumberSets(workoutExercise.getId());
        recalculatePersonalRecords(workoutExercise);
        recalculateWorkoutTotals(workoutExercise.getWorkoutSession());
    }


    @Override
    @Transactional
    public void removeExercise(Long workoutId, Long workoutExerciseId) {
        User user = userService.getCurrentUser();
        WorkoutExercise workoutExercise = workoutExerciseRepository
                .findByIdAndWorkoutSession_User_Id(workoutExerciseId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Workout exercise not found"));

        WorkoutSession session = workoutExercise.getWorkoutSession();

        if (!session.getId().equals(workoutId)) {
            throw new ResourceNotFoundException("Workout exercise not found");
        }
//
//        if (session.getCompleted()) {
//            throw new BadRequestException("Completed workout cannot be modified");
//        }

        workoutExerciseRepository.delete(workoutExercise);
        workoutExerciseRepository.flush();
        renumberExercises(session.getId());
        recalculateWorkoutTotals(workoutExercise.getWorkoutSession());
    }

    @Override
    public ExerciseProgressDto getExerciseProgress(Long exerciseId) {
        User user = userService.getCurrentUser();
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));

        List<WorkoutExercise> exerciseHistory = workoutExerciseRepository
                        .findByWorkoutSession_User_IdAndExercise_IdAndWorkoutSession_CompletedTrueOrderByWorkoutSession_StartedAtAsc(
                                user.getId(), exerciseId);

        List<ExerciseProgressPointDto> history = exerciseHistory.stream()
                        .map(this::toProgressPoint).toList();

        double currentBestEstimatedOneRepMax = history.stream()
                        .map(ExerciseProgressPointDto
                                ::getBestEstimatedOneRepMax)
                        .filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .max()
                        .orElse(0.0);

        long totalPersonalRecords = history.stream().filter(ExerciseProgressPointDto
                                ::getPersonalRecord).count();

        return ExerciseProgressDto.builder()
                .exerciseId(exercise.getId())
                .exerciseName(exercise.getName())
                .currentBestEstimatedOneRepMax(
                        round(currentBestEstimatedOneRepMax))
                .totalPersonalRecords(totalPersonalRecords)
                .history(history)
                .build();
    }
    @Override
    @Transactional
    public WorkoutSessionResponseDto renameWorkout(Long workoutId, RenameWorkoutRequestDto request) {
        User user = userService.getCurrentUser();
        WorkoutSession session = workoutSessionRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Workout session not found"));
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Workout name is required");
        }
        session.setName(request.getName().trim());
        WorkoutSession updatedSession = workoutSessionRepository.save(session);
        return workoutMapper.toSessionResponseDto(updatedSession);
    }

    @Override
    public TrainingVolumeAnalyticsDto getTrainingVolumeAnalytics(WorkoutAnalyticsPeriod period) {
        User user = userService.getCurrentUser();
        LocalDate today = LocalDate.now();
        LocalDate historyStart = period == WorkoutAnalyticsPeriod.WEEKLY
                ? today.minusWeeks(7)
                : today.minusMonths(5);

        LocalDateTime startDateTime = historyStart.atStartOfDay();
        LocalDateTime endDateTime = today.plusDays(1).atStartOfDay();

        List<WorkoutSession> sessions = workoutSessionRepository.findByUserAndCompletedTrueAndStartedAtBetweenOrderByStartedAtAsc(
                                user,
                                startDateTime,
                                endDateTime);

        List<TrainingVolumePointDto> history =
                period == WorkoutAnalyticsPeriod.WEEKLY
                        ? buildWeeklyVolumeHistory(
                        sessions,
                        today)
                        : buildMonthlyVolumeHistory(
                        sessions,
                        today);

        TrainingVolumePointDto currentPeriod = history.get(history.size() - 1);

        TrainingVolumePointDto previousPeriod = history.size() >= 2 ? history.get(history.size() - 2) : null;

        double currentVolume = currentPeriod.getTotalVolume();

        double previousVolume =
                previousPeriod == null
                        ? 0.0
                        : previousPeriod.getTotalVolume();

        double changePercentage = calculateVolumeChangePercentage(currentVolume, previousVolume);

        return TrainingVolumeAnalyticsDto.builder()
                .period(period)
                .currentPeriodVolume(round(currentVolume))
                .previousPeriodVolume(round(previousVolume))
                .volumeChangePercentage(
                        round(changePercentage))
                .history(history)
                .build();
    }

    //helpers

    //renumber sets
    private void renumberExercises(Long workoutSessionId) {

        List<WorkoutExercise> exercises = workoutExerciseRepository
                        .findByWorkoutSessionIdOrderByExerciseOrderAsc(
                                workoutSessionId);
        for (int i = 0; i < exercises.size(); i++) {
            exercises.get(i).setExerciseOrder(i + 1);
        }
        workoutExerciseRepository.saveAll(exercises);
    }

    //renumber sets
    private void renumberSets(Long workoutExerciseId) {
        List<WorkoutSet> sets = workoutSetRepository
                .findByWorkoutExerciseIdOrderBySetNumberAsc(
                        workoutExerciseId);
        for (int i = 0; i < sets.size(); i++) {
            sets.get(i).setSetNumber(i + 1);
        }
        workoutSetRepository.saveAll(sets);
    }

    private void validateSet(AddWorkoutSetRequestDto request) {
        if (request.getWeight() == null) {
            throw new BadRequestException("Weight is required");
        }
        if (request.getReps() == null) {
            throw new BadRequestException("Reps are required");
        }
        if (request.getWeight() < 0) {
            throw new BadRequestException("Weight cannot be negative");
        }
        if (request.getReps() <= 0) {
            throw new BadRequestException("Reps must be greater than zero");
        }
    }


    //pr detection helper

    //recalculate set which is  helper method
    private void recalculateSet(WorkoutSet workoutSet) {
        double volume = workoutSet.getWeight() * workoutSet.getReps();
        double estimatedOneRepMax = workoutSet.getWeight() * (1 + workoutSet.getReps() / 30.0);
        workoutSet.setVolume(round(volume));
        workoutSet.setEstimatedOneRepMax(round(estimatedOneRepMax));
    }

    //round to round off float / double numbers
    private Double round(Double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    //progress-point
    private ExerciseProgressPointDto toProgressPoint(WorkoutExercise workoutExercise) {

        List<WorkoutSet> sets = workoutSetRepository.findByWorkoutExerciseIdOrderBySetNumberAsc(
                                workoutExercise.getId());

        double bestEstimatedOneRepMax =
                sets.stream()
                        .map(WorkoutSet::getEstimatedOneRepMax)
                        .filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .max()
                        .orElse(0.0);

        boolean personalRecord = sets.stream().anyMatch(set -> Boolean.TRUE.equals(set.getPersonalRecord()));
        return ExerciseProgressPointDto.builder()
                .workoutId(
                        workoutExercise
                                .getWorkoutSession()
                                .getId())
                .workoutName(
                        workoutExercise
                                .getWorkoutSession()
                                .getName())
                .performedAt(
                        workoutExercise
                                .getWorkoutSession()
                                .getStartedAt())
                .totalVolume(
                        workoutExercise.getTotalVolume())
                .bestEstimatedOneRepMax(
                        round(bestEstimatedOneRepMax))
                .personalRecord(personalRecord)
                .build();
    }

    //analytics helpers
    private List<TrainingVolumePointDto> buildWeeklyVolumeHistory(List<WorkoutSession> sessions, LocalDate today) {
        List<TrainingVolumePointDto> history = new ArrayList<>();
        LocalDate currentWeekStart = today.with(DayOfWeek.MONDAY);

        for (int i = 7; i >= 0; i--) {
            LocalDate periodStart = currentWeekStart.minusWeeks(i);
            LocalDate periodEnd = periodStart.plusDays(6);

            double totalVolume = sessions.stream()
                    .filter(session -> {
                        LocalDate workoutDate = session.getStartedAt().toLocalDate();
                        return !workoutDate.isBefore(periodStart) && !workoutDate.isAfter(periodEnd);
                    })
                    .map(WorkoutSession::getTotalVolume)
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .sum();

            history.add(
                    TrainingVolumePointDto.builder()
                            .periodStart(periodStart)
                            .periodEnd(periodEnd)
                            .totalVolume(round(totalVolume))
                            .build()
            );
        }
        return history;
    }

    // helper for monthly analytics
    private List<TrainingVolumePointDto> buildMonthlyVolumeHistory(List<WorkoutSession> sessions, LocalDate today) {
        List<TrainingVolumePointDto> history = new ArrayList<>();
        LocalDate currentMonthStart = today.withDayOfMonth(1);

        for (int i = 5; i >= 0; i--) {
            LocalDate periodStart = currentMonthStart.minusMonths(i);
            LocalDate periodEnd = periodStart.withDayOfMonth(periodStart.lengthOfMonth());

            double totalVolume = sessions.stream()
                    .filter(session -> {
                        LocalDate workoutDate = session.getStartedAt().toLocalDate();
                        return !workoutDate.isBefore(periodStart) && !workoutDate.isAfter(periodEnd);
                    })
                    .map(WorkoutSession::getTotalVolume)
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .sum();

            history.add(
                    TrainingVolumePointDto.builder()
                            .periodStart(periodStart)
                            .periodEnd(periodEnd)
                            .totalVolume(round(totalVolume))
                            .build()
            );
        }

        return history;
    }

    private double calculateVolumeChangePercentage(double currentVolume, double previousVolume) {
        if (previousVolume == 0) {
            return currentVolume > 0 ? 100.0 : 0.0;
        }
        return ((currentVolume - previousVolume) / previousVolume) * 100;
    }

    @Transactional
    private void recalculateWorkoutTotals(WorkoutSession session) {
        List<WorkoutExercise> exercises = workoutExerciseRepository
                        .findByWorkoutSessionIdOrderByExerciseOrderAsc(session.getId());
        double workoutTotalVolume = 0.0;
        for (WorkoutExercise exercise : exercises) {
            List<WorkoutSet> sets = workoutSetRepository
                    .findByWorkoutExerciseIdOrderBySetNumberAsc(exercise.getId());

            double exerciseTotalVolume = sets.stream()
                    .map(WorkoutSet::getVolume)
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .sum();

            exercise.setTotalVolume(round(exerciseTotalVolume));
            workoutTotalVolume += exerciseTotalVolume;
        }
        workoutExerciseRepository.saveAll(exercises);
        session.setTotalVolume(round(workoutTotalVolume));
        workoutSessionRepository.save(session);
    }
    private void recalculatePersonalRecords(WorkoutExercise workoutExercise) {
        List<WorkoutSet> sets = workoutSetRepository
                        .findByWorkoutExerciseIdOrderBySetNumberAsc(
                                workoutExercise.getId());

        if (sets.isEmpty()) {return;}
        Long exerciseId = workoutExercise.getExercise().getId();

        Long userId = workoutExercise.getWorkoutSession().getUser().getId();
        Long workoutId = workoutExercise.getWorkoutSession().getId();

        Double previousBest = workoutSetRepository
                        .findMaxEstimatedOneRepMaxExcludingWorkout(exerciseId, userId, workoutId)
                        .orElse(null);
        sets.forEach(set -> set.setPersonalRecord(false));
        WorkoutSet bestSet = sets.stream().max((set1, set2) ->
                                Double.compare(set1.getEstimatedOneRepMax(), set2.getEstimatedOneRepMax()))
                        .orElse(null);

        if (bestSet != null && (previousBest == null || bestSet.getEstimatedOneRepMax() > previousBest)) {
            bestSet.setPersonalRecord(true);
        }
        workoutSetRepository.saveAll(sets);
    }
}

