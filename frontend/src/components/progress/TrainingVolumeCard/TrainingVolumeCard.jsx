import { useMemo } from "react";
import { formatNumber } from "../../../utils";
import "./TrainingVolumeCard.css";

function TrainingVolumeCard({ period, onPeriodChange, data, loading, error }) {
    const history = useMemo(() => {
        if (!data?.history) return [];
        return normalizeHistory(data.history, period);
    }, [data, period]);

    const maxVolume = Math.max(...history.map(point => point.totalVolume ?? 0), 1);
    const change = data?.volumeChangePercentage ?? 0;
    const isPositive = change > 0;
    const isNegative = change < 0;

    return (
        <section className="training-volume-card">
            <div className="analytics-card-header">
                <span className="analytics-card-label">TRAINING VOLUME</span>

                <div className="period-toggle">
                    <button
                        type="button"
                        className={period === "WEEKLY" ? "active" : ""}
                        onClick={() => onPeriodChange("WEEKLY")}
                    >
                        Weekly
                    </button>
                    <button
                        type="button"
                        className={period === "MONTHLY" ? "active" : ""}
                        onClick={() => onPeriodChange("MONTHLY")}
                    >
                        Monthly
                    </button>
                </div>
            </div>

            {loading ? (
                <div className="analytics-loading">Loading training volume...</div>
            ) : error ? (
                <div className="analytics-error">{error}</div>
            ) : (
                <>
                    <div className="volume-summary">
                        <strong>{formatNumber(data?.currentPeriodVolume ?? 0)} kg</strong>

                        <span className={`volume-change ${isPositive ? "positive" : isNegative ? "negative" : ""}`}>
                            {isPositive && "↑ "}{isNegative && "↓ "}{formatNumber(Math.abs(change))}%
                        </span>

                        <span className="previous-volume">
                            vs previous period ({formatNumber(data?.previousPeriodVolume ?? 0)} kg)
                        </span>
                    </div>

                    <div className="volume-chart-container">
                        <div className="volume-chart">
                            {history.length === 0 ? (
                                <div className="chart-empty">No training volume data yet.</div>
                            ) : (
                                history.map((point, index) => {
                                    const volume = point.totalVolume ?? 0;
                                    const height = volume === 0 ? 0 : Math.max(4, (volume / maxVolume) * 100);
                                    const isCurrent = index === history.length - 1;

                                    return (
                                        <div
                                            className="volume-bar-wrapper"
                                            key={`${point.periodStart}-${index}`}
                                        >
                                            <div className="volume-bar-area">
                                                <div
                                                    className={`volume-bar ${isCurrent ? "current" : ""}`}
                                                    style={{ height: `${height}%` }}
                                                >
                                                    <div className="volume-bar-tooltip">
                                                        {formatNumber(volume)} kg
                                                    </div>
                                                </div>
                                            </div>

                                            <span className="volume-bar-label">
                                                {formatPeriodLabel(
                                                    point.periodStart,
                                                    period,
                                                    index,
                                                    history.length
                                                )}
                                            </span>
                                        </div>
                                    );
                                })
                            )}
                        </div>
                    </div>
                </>
            )}
        </section>
    );
}

function normalizeHistory(history, period) {
    if (!history.length) return [];

    const sorted = [...history].sort(
        (a, b) => new Date(a.periodStart) - new Date(b.periodStart)
    );

    const firstDate = new Date(sorted[0].periodStart);
    const lastDate = new Date(sorted[sorted.length - 1].periodStart);
    const result = [];
    const current = new Date(firstDate);

    while (current <= lastDate) {
        const dateKey = current.toISOString().split("T")[0];
        const existing = sorted.find(point => point.periodStart === dateKey);

        result.push(
            existing ?? {
                periodStart: dateKey,
                periodEnd: dateKey,
                totalVolume: 0
            }
        );

        if (period === "WEEKLY") {
            current.setDate(current.getDate() + 7);
        } else {
            current.setMonth(current.getMonth() + 1);
        }
    }

    return result;
}

function formatPeriodLabel(periodStart, period, index, total) {
    if (period === "WEEKLY") {
        if (index === total - 1) return "This wk";
        const weeksAgo = total - index;
        if (index === 0) return `${weeksAgo} wks ago`;
        return `${weeksAgo}`;
    }

    const date = new Date(`${periodStart}T00:00:00`);
    return date.toLocaleDateString("en-US", { month: "short" });
}

export default TrainingVolumeCard;