import api from './api';

export const exerciseService = {
  
  // @GetMapping
  getAllExercises: async (page = 0) => {
    const response = await api.get('/exercises', {
      params: { page }
    });
    return response.data;
  },

  // @GetMapping("/{id}")
  getExerciseById: async (id) => {
    const response = await api.get(`/exercises/${id}`);
    return response.data;
  },

  // @GetMapping("/search")
  searchExercises: async (keyword) => {
    const response = await api.get('/exercises/search', {
      params: { keyword }
    });
    return response.data;
  },

  // @GetMapping("/filter")
  filterExercises: async (filterParams) => {
    // filterParams can include: keyword, category, primaryMuscle,
    // equipment, difficulty, exerciseType, sortBy, direction, page
    const response = await api.get('/exercises/filter', {
      params: filterParams
    });
    return response.data;
  },

  // @GetMapping("/metadata")
  getExerciseMetadata: async () => {
    const response = await api.get('/exercises/metadata');
    return response.data;
  },

  // @GetMapping("/category/{category}/primary-muscles")
  getPrimaryMusclesByCategory: async (category) => {
    const response = await api.get(
      `/exercises/category/${category}/primary-muscles`
    );
    return response.data;
  }

};