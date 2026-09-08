import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid
} from "recharts";
import CustomSelect from "../../common/CustomSelect";
import "./MeasurementTrend.css";

function MeasurementTrend({
  trend,
  selectedMetric,
  onMetricChange,
  metricOptions
}) {
  return (
    <section className="measurements-section">
      <div className="trend-section-header">
        <span className="section-title-text">TREND</span>
        
        <div className="trend-actions-right">
          <CustomSelect
            value={selectedMetric}
            onChange={(e) => onMetricChange(e.target.value)}
            options={metricOptions}
          />
        </div>
      </div>

      <div className="trend-container">
        {trend.length === 0 ? (
          <div className="trend-empty">
            No data available for this metric yet.
          </div>
        ) : (
          <ResponsiveContainer width="100%" height={260}>
            <LineChart
              data={trend}
              margin={{
                top: 3,
                right: 5,
                left: -20,
                bottom: 0
              }}
            >
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              
              <XAxis
                dataKey="recordedAt"
                tickFormatter={(value) =>
                  new Date(value).toLocaleDateString("en-IN", {
                    month: "short",
                    day: "numeric"
                  })
                }
              />
              
              <YAxis
                domain={["dataMin - 1", "dataMax + 1"]}
              />
              
              <Tooltip
                labelFormatter={(value) =>
                  new Date(value).toLocaleDateString("en-IN", {
                    month: "short",
                    day: "numeric",
                    year: "numeric"
                  })
                }
              />
              
              <Line
                type="monotone"
                dataKey="value"
                stroke="var(--fs-gold)"
                strokeWidth={2.5}
                dot={{ r: 4 }}
                activeDot={{ r: 6 }}
              />
            </LineChart>
          </ResponsiveContainer>
        )}
      </div>
    </section>
  );
}

export default MeasurementTrend;