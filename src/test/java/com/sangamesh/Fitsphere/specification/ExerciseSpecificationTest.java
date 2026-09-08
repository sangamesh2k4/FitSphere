package com.sangamesh.Fitsphere.specification;

import com.sangamesh.Fitsphere.entity.Exercise;
import com.sangamesh.Fitsphere.enums.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ExerciseSpecificationTest {

    @Mock
    private Root<Exercise> root;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Predicate predicate;

    @Mock
    private Path<String> stringPath;

    @Mock
    private Path<Object> objectPath;

    @Test
    void hasCategory_whenCategoryProvided_shouldCreateEqualPredicate() {

        when(root.get("category")).thenReturn(objectPath);
        when(cb.equal(objectPath, Category.CHEST)).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasCategory(Category.CHEST)
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(root).get("category");
        verify(cb).equal(objectPath, Category.CHEST);
    }

    @Test
    void hasCategory_whenCategoryNull_shouldReturnConjunction() {

        when(cb.conjunction()).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasCategory(null)
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(cb).conjunction();
        verify(root, never()).get(anyString());
    }

    @Test
    void hasPrimaryMuscle_whenProvided_shouldCreateEqualPredicate() {

        when(root.get("primaryMuscle")).thenReturn(objectPath);
        when(cb.equal(objectPath, Muscle.UPPER_CHEST)).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasPrimaryMuscle(Muscle.UPPER_CHEST)
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(root).get("primaryMuscle");
        verify(cb).equal(objectPath, Muscle.UPPER_CHEST);
    }

    @Test
    void hasPrimaryMuscle_whenNull_shouldReturnConjunction() {

        when(cb.conjunction()).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasPrimaryMuscle(null)
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(cb).conjunction();
        verify(root, never()).get(anyString());
    }

    @Test
    void hasEquipment_whenProvided_shouldCreateEqualPredicate() {

        when(root.get("equipment")).thenReturn(objectPath);
        when(cb.equal(objectPath, Equipment.BARBELL)).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasEquipment(Equipment.BARBELL)
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(root).get("equipment");
        verify(cb).equal(objectPath, Equipment.BARBELL);
    }

    @Test
    void hasEquipment_whenNull_shouldReturnConjunction() {

        when(cb.conjunction()).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasEquipment(null)
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(cb).conjunction();
        verify(root, never()).get(anyString());
    }

    @Test
    void hasDifficulty_whenProvided_shouldCreateEqualPredicate() {

        when(root.get("difficulty")).thenReturn(objectPath);
        when(cb.equal(objectPath, Difficulty.INTERMEDIATE)).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasDifficulty(Difficulty.INTERMEDIATE)
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(root).get("difficulty");
        verify(cb).equal(objectPath, Difficulty.INTERMEDIATE);
    }

    @Test
    void hasDifficulty_whenNull_shouldReturnConjunction() {

        when(cb.conjunction()).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasDifficulty(null)
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(cb).conjunction();
        verify(root, never()).get(anyString());
    }

    @Test
    void hasExerciseType_whenProvided_shouldCreateEqualPredicate() {

        when(root.get("exerciseType")).thenReturn(objectPath);
        when(cb.equal(objectPath, ExerciseType.COMPOUND)).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasExerciseType(ExerciseType.COMPOUND)
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(root).get("exerciseType");
        verify(cb).equal(objectPath, ExerciseType.COMPOUND);
    }

    @Test
    void hasExerciseType_whenNull_shouldReturnConjunction() {

        when(cb.conjunction()).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasExerciseType(null)
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(cb).conjunction();
        verify(root, never()).get(anyString());
    }

    @Test
    void hasKeyword_whenKeywordProvided_shouldCreateLikePredicate() {

        when(root.<String>get("name")).thenReturn(stringPath);
        when(cb.lower(stringPath)).thenReturn(stringPath);
        when(cb.like(stringPath, "%bench%")).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasKeyword("Bench")
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(root).get("name");
        verify(cb).lower(stringPath);
        verify(cb).like(stringPath, "%bench%");
    }

    @Test
    void hasKeyword_whenKeywordIsBlank_shouldReturnConjunction() {

        when(cb.conjunction()).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasKeyword("   ")
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(cb).conjunction();
        verify(root, never()).get(anyString());
    }

    @Test
    void hasKeyword_whenKeywordIsNull_shouldReturnConjunction() {

        when(cb.conjunction()).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasKeyword(null)
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(cb).conjunction();
        verify(root, never()).get(anyString());
    }

    @Test
    void isActive_shouldCreateIsTruePredicate() {

        when(root.get("active")).thenReturn(objectPath);
        when(cb.isTrue(any())).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.isActive()
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(root).get("active");
        verify(cb).isTrue(any());
    }

    @Test
    void hasActive_whenActiveProvided_shouldCreateEqualPredicate() {

        when(root.get("active")).thenReturn(objectPath);
        when(cb.equal(objectPath, true)).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasActive(true)
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(root).get("active");
        verify(cb).equal(objectPath, true);
    }

    @Test
    void hasActive_whenActiveFalse_shouldCreateEqualPredicate() {

        when(root.get("active")).thenReturn(objectPath);
        when(cb.equal(objectPath, false)).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasActive(false)
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(root).get("active");
        verify(cb).equal(objectPath, false);
    }

    @Test
    void hasActive_whenNull_shouldReturnConjunction() {

        when(cb.conjunction()).thenReturn(predicate);

        Predicate result =
                ExerciseSpecification.hasActive(null)
                        .toPredicate(root, null, cb);

        assertSame(predicate, result);

        verify(cb).conjunction();
        verify(root, never()).get(anyString());
    }
}