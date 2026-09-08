// src/utils/badgeUtils.js

export const getBadgeType = (label) => {
    if (!label) return "info";

    const value = label.toLowerCase();

    switch (value) {
        case "underweight":
        case "fair":
        case "overweight":
            return "warning";

        case "obese":
        case "poor":
        case "needs improvement":
            return "danger";

        case "normal":
        case "fitness":
        case "excellent":
        case "very good":
            return "success";

        default:
            return "info";
    }
};

export const getBadgeIcon = (type) => {
    switch (type) {
        case "success":
            return "🟢";

        case "warning":
            return "🟡";

        case "danger":
            return "🔴";

        default:
            return "🔵";
    }
};