// src/utils/formatters.js

export const formatNumber = (value, decimals = 1) => {
    if (value === null || value === undefined || isNaN(value)) {
        return "--";
    }

    return Number(value).toFixed(decimals);
};

export const formatCalories = (value) => {
    if (value === null || value === undefined || isNaN(value)) {
        return "--";
    }

    return `${Math.round(value)} kcal`;
};

export const formatGrams = (value) => {
    if (value === null || value === undefined || isNaN(value)) {
        return "--";
    }

    return `${Math.round(value)} g`;
};

export const formatLiters = (value) => {
    if (value === null || value === undefined || isNaN(value)) {
        return "--";
    }

    return `${(Number(value)/1000).toFixed(1)} L/day`;
};

export const formatPercentage = (value) => {
    if (value === null || value ===undefined || isNaN(value)) {
        return "--";
    }

    return `${Number(value).toFixed(1)}%`;
};