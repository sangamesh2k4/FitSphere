import {
    createContext,
    useCallback,
    useEffect,
    useMemo,
    useState
} from "react";

import favoriteService from "../services/favoriteService";
import { useAuth } from "../hooks/useAuth";

export const FavoritesContext = createContext(null);

export const FavoritesProvider = ({ children }) => {

    const { isAuthenticated } = useAuth();

    const [loading, setLoading] = useState(false);

    const [exerciseFavoriteIds, setExerciseFavoriteIds] =
        useState(new Set());

    const [foodFavoriteIds, setFoodFavoriteIds] =useState(new Set());

    const loadFavorites = useCallback(async () => {
        if (!isAuthenticated) {
            setExerciseFavoriteIds(new Set());
            setFoodFavoriteIds(new Set());
            return;
        }

        try {
            setLoading(true);
            const [
                exerciseFavorites,
                foodFavorites
            ] = await Promise.all([

                favoriteService.getUserFavorites(),

                favoriteService.getUserFoodFavorites()

            ]);

            setExerciseFavoriteIds(

                new Set(
                    exerciseFavorites.map(
                        exercise => exercise.id
                    )
                )

            );

            setFoodFavoriteIds(

                new Set(
                    foodFavorites.map(
                        food => food.fdcId
                    )
                )

            );

        } catch (err) {

            console.error(err);

        } finally {

            setLoading(false);

        }

    }, [isAuthenticated]);

    useEffect(() => {

        loadFavorites();

    }, [loadFavorites]);

    //--------------------------------------------------

    const isExerciseFavorite = useCallback(

        (exerciseId) =>
            exerciseFavoriteIds.has(exerciseId),

        [exerciseFavoriteIds]

    );

    const isFoodFavorite = useCallback(

        (fdcId) =>
            foodFavoriteIds.has(fdcId),

        [foodFavoriteIds]

    );

    //--------------------------------------------------

    const toggleExerciseFavorite =
        useCallback(async (exerciseId) => {

            if (!isAuthenticated) return;

            try {

                if (exerciseFavoriteIds.has(exerciseId)) {

                    await favoriteService.removeFavorite(
                        exerciseId
                    );

                    setExerciseFavoriteIds(prev => {

                        const next = new Set(prev);

                        next.delete(exerciseId);

                        return next;

                    });

                } else {

                    await favoriteService.addFavorite(
                        exerciseId
                    );

                    setExerciseFavoriteIds(prev => {

                        const next = new Set(prev);

                        next.add(exerciseId);

                        return next;

                    });

                }

            } catch (err) {

                console.error(err);

            }

        }, [
            exerciseFavoriteIds,
            isAuthenticated
        ]);

    //--------------------------------------------------

    const toggleFoodFavorite =
        useCallback(async (fdcId) => {

            if (!isAuthenticated) return;

            try {

                if (foodFavoriteIds.has(fdcId)) {

                    await favoriteService.removeFoodFavorite(
                        fdcId
                    );

                    setFoodFavoriteIds(prev => {

                        const next = new Set(prev);

                        next.delete(fdcId);

                        return next;

                    });

                } else {

                    await favoriteService.addFoodFavorite(
                        fdcId
                    );

                    setFoodFavoriteIds(prev => {

                        const next = new Set(prev);

                        next.add(fdcId);

                        return next;

                    });

                }

            } catch (err) {

                console.error(err);

            }

        }, [
            foodFavoriteIds,
            isAuthenticated
        ]);

    //--------------------------------------------------

    const value = useMemo(() => ({
        loading,
        loadFavorites,
        isExerciseFavorite,
        isFoodFavorite,
        toggleExerciseFavorite,
        toggleFoodFavorite,
        exerciseFavoriteIds,
        foodFavoriteIds

    }), [

        loading,
        loadFavorites,
        isExerciseFavorite,
        isFoodFavorite,
        toggleExerciseFavorite,
        toggleFoodFavorite,
        exerciseFavoriteIds,
        foodFavoriteIds
    ]);

    return (
        <FavoritesContext.Provider value={value}>
            {children}
        </FavoritesContext.Provider>

    );

};