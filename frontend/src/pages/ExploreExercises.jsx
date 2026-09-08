import { useCallback, useEffect, useRef, useState } from 'react'
import { useLocation } from "react-router-dom"
import ExerciseCard from '../components/exercises/ExerciseCard'
import { exerciseService } from '../services/exerciseService'
import { formatEnum } from '../utils/formatEnum'
import '../css/ExerciseCard.css'
import '../css/ExploreExercises.css'
import ExerciseCardSkeleton from '../components/skeleton/ExerciseCardSkeleton';

function ExploreExercises() {
  // Split loading states for much cleaner UI rendering
  const [isInitialLoading, setIsInitialLoading] = useState(true)
  const [isLoadingMore, setIsLoadingMore] = useState(false)
  
  const [exercises, setExercises] = useState([])
  const [totalExercises, setTotalExercises] = useState(0)

  const [search, setSearch] = useState('')
  const [equipment, setEquipment] = useState('')
  const [difficulty, setDifficulty] = useState('')
  const [category, setCategory] = useState('')

  const [metadata, setMetadata] = useState(null)

  const [page, setPage] = useState(0)
  const [hasMore, setHasMore] = useState(true)
  
  const loadMoreRef = useRef(null)
  
  // requestIdRef prevents race conditions and stale data overwriting newer requests
  const requestIdRef = useRef(0)
  const location = useLocation();

  const detailsBasePath = location.pathname.startsWith("/app")
    ? "/app/exercises"
    : "/exercises";

  // 1. Fetch metadata once on mount
  useEffect(() => {
    const fetchMetadata = async () => {
      try {
        const data = await exerciseService.getExerciseMetadata()
        setMetadata(data)
      } catch (error) {
        console.error("Failed to load metadata", error)
      }
    }
    
    fetchMetadata()
  }, [])

  // 2. Fetch function utilizing the requestId pattern
  const fetchExercises = useCallback(async (pageNumber = 0, append = false) => {
    // Increment request ID on every new fetch call
    const currentRequestId = ++requestIdRef.current;

    if (append) {
      setIsLoadingMore(true)
    } else {
      setIsInitialLoading(true)
    }

    const filterParams = {
      page: pageNumber,
      ...(search.trim() && { keyword: search.trim() }),
      ...(equipment && { equipment }),
      ...(difficulty && { difficulty }),
      ...(category && { category })
    }

    try {
      const data = await exerciseService.filterExercises(filterParams)
      
      // RACING CONDITION GUARD: If a newer request was fired while this one was pending, ignore this result.
      if (currentRequestId !== requestIdRef.current) {
        return;
      }
      
      setExercises(previous => append ? [...previous, ...data.content] : data.content)
      setTotalExercises(data.totalElements)
      setPage(data.number)
      setHasMore(!data.last)
    } catch (error) {
      // Only log errors for the most recent request
      if (currentRequestId === requestIdRef.current) {
        console.error("Failed to fetch exercises", error)
      }
    } finally {
      // Only clear loading states if this is still the active request
      if (currentRequestId === requestIdRef.current) {
        setIsInitialLoading(false)
        setIsLoadingMore(false)
      }
    }
  }, [search, equipment, difficulty, category])

  // 3. Debounced Filter Effect
  useEffect(() => {
    const timer = setTimeout(() => {
      fetchExercises(0, false)
    }, 500)

    return () => clearTimeout(timer)
  }, [fetchExercises])

  // 4. Infinite Scroll Observer
  useEffect(() => {
    // If we are already loading (initial or more) or have no data, don't observe yet.
    if (!hasMore || isInitialLoading || isLoadingMore || exercises.length === 0) return

    const observer = new IntersectionObserver(
      entries => {
        if (entries[0].isIntersecting) {
          fetchExercises(page + 1, true)
        }
      },
      { 
        threshold: 0, 
        rootMargin: "200px" // Start loading 200px before reaching the bottom for smoother UX
      }
    )

    if (loadMoreRef.current) {
      observer.observe(loadMoreRef.current)
    }

    return () => observer.disconnect()
  }, [page, hasMore, isInitialLoading, isLoadingMore, fetchExercises, exercises.length])

  const resetFilters = () => {
    setSearch('')
    setEquipment('')
    setDifficulty('')
    setCategory('')
  }

  return (
    <main className="explore-exercises-container">
      
      <div className="exercise-library-header">
        <h1>EXPLORE EXERCISES</h1>
        <p>
          Discover 250+ exercises with step-by-step instructions, tutorial video
          demonstrations, equipment details, and muscle-specific guidance.
        </p>
      </div>

      <div className="exercise-filters">
        <input
          type="text"
          placeholder="Search exercises..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        
        <select value={category} onChange={(e) => setCategory(e.target.value)}>
          <option value="">All Categories</option>
          {metadata?.categories?.map(item => (
            <option key={item} value={item}>{formatEnum(item)}</option>
          ))}
        </select>

        <select value={equipment} onChange={(e) => setEquipment(e.target.value)}>
          <option value="">All Equipment</option>
          {metadata?.equipment?.map(item => (
            <option key={item} value={item}>{formatEnum(item)}</option>
          ))}
        </select>

        <select value={difficulty} onChange={(e) => setDifficulty(e.target.value)}>
          <option value="">All Difficulties</option>
          {metadata?.difficulties?.map(item => (
            <option key={item} value={item}>{formatEnum(item)}</option>
          ))}
        </select>

        {(search.trim() !== '' || equipment !== '' || difficulty !== '' || category !== '') && (
          <button
            className="secondary-button"
            onClick={resetFilters}
          >
            Clear Filters
          </button>
        )}
      </div>

      <p className="exercise-count">Showing {totalExercises} exercises</p>
  
      {/* 1. Initial Page Load Skeleton */}
      {isInitialLoading && (
        <div className="exercise-grid">
          <ExerciseCardSkeleton count={6} />
        </div>
      )}

      {/* 2. Populated Data & Infinite Scroll State */}
      {!isInitialLoading && exercises.length > 0 && (
        <>
          <div className="exercise-grid">
            {exercises.map((exercise) => (
              <ExerciseCard
                key={exercise.id}
                exercise={exercise}
                detailsPath={`${detailsBasePath}/${exercise.id}`}
              />
            ))}
            
            {/* Load More Skeleton appended seamlessly to the end of the grid */}
            {isLoadingMore && (
              <ExerciseCardSkeleton count={3} />
            )}
          </div>

          {/* Sentinel element for infinite scroll */}
          <div ref={loadMoreRef} style={{ height: "20px" }} />
        </>
      )}

      {/* 3. Empty State */}
      {!isInitialLoading && exercises.length === 0 && (
        <div className="empty-state">
          <h2>No Exercises Found</h2>
          <p>Try changing your search or clearing your filters.</p>
        </div>
      )}

    </main>
  )
}

export default ExploreExercises