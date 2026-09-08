import { ArrowDown, ArrowUp, Minus, ChevronRight } from "lucide-react";
import { formatNumber } from "../../../utils";

import "./DashboardMetricCard.css";

function DashboardMetricCard({
    label,
    value,
    unit,
    change,
    changeUnit,
    onClick,
    decreaseIsPositive = true
}) {
    const hasValue = value !== null && value !== undefined;
    const hasChange = change !== null && change !== undefined;

    const getTrendClass = () => {
        if (!hasChange || change === 0) {
            return "metric-trend-neutral";
        }

        const isDecrease = change < 0;

        const isPositive = decreaseIsPositive
            ? isDecrease
            : !isDecrease;

        return isPositive
            ? "metric-trend-positive"
            : "metric-trend-negative";
    };

    return (
        <button
            type="button"
            className="dashboard-metric-card"
            onClick={onClick}
        >
            <span className="dashboard-metric-label">
                {label}
            </span>

            <div className="dashboard-metric-main">

                <div className="dashboard-metric-value">
                    {hasValue ? (
                        <>
                            <strong>
                                {formatNumber(value)}
                            </strong>

                            <span>
                                {unit}
                            </span>
                        </>
                    ) : (
                        <strong className="metric-no-data">
                            No data yet
                        </strong>
                    )}
                </div>

                {hasChange && (
                    <span
                        className={`dashboard-metric-trend ${getTrendClass()}`}
                    >
                        {change === 0 ? (
                            <Minus size={13} />
                        ) : change < 0 ? (
                            <ArrowDown size={13} />
                        ) : (
                            <ArrowUp size={13} />
                        )}

                        <span>
                            {formatNumber(Math.abs(change))}
                            {changeUnit}
                        </span>
                    </span>
                )}

            </div>

            <ChevronRight
                className="dashboard-metric-arrow"
                size={20}
            />
        </button>
    );
}

export default DashboardMetricCard;