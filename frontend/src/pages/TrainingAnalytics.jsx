import { useEffect, useState } from 'react'
import { workoutService } from '../services/workoutService'
import { formatDate, formatNumber } from '../utils'
import '../css/TrainingAnalytics.css'

function TrainingAnalytics() {
  const [period, setPeriod] = useState('WEEK')
  const [analytics, setAnalytics] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)

  const fetchAnalytics = async () => {
    try {
      setLoading(true)
      setError(false)
      const data = await workoutService.getTrainingVolumeAnalytics(period)
      setAnalytics(data)
    } catch (err) {
      console.error('Failed to fetch analytics', err)
      setError(true)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchAnalytics()
  }, [period])

  const renderTrend = (percentage) => {
    if (percentage == null) return <span className="stat-value neutral">0%</span>
    if (percentage > 0) return <span className="stat-value positive">+{percentage.toFixed(1)}%</span>
    if (percentage < 0) return <span className="stat-value negative">{percentage.toFixed(1)}%</span>
    return <span className="stat-value neutral">0%</span>
  }

  const maxChartValue = analytics?.history?.length 
    ? Math.max(...analytics.history.map(point => point.totalVolume || 0), 1) 
    : 1

  return (
    <main className="analytics-wrapper">
      <header className="analytics-header">
        <div>
          <p className="section-label">ANALYTICS</p>
          <h1>TRAINING VOLUME</h1>
        </div>

        <div className="period-selector">
          <select value={period} onChange={(e) => setPeriod(e.target.value)}>
            <option value="WEEK">Week</option>
            <option value="MONTH">Month</option>
            <option value="YEAR">Year</option>
          </select>
        </div>
      </header>

      {loading ? (
        <div className="loading-state">Loading your data...</div>
      ) : error || !analytics ? (
        <div className="empty-state">Failed to load analytics.</div>
      ) : (
        <>
          <div className="analytics-stats-grid">
            <div className="stat-card">
              <span className="stat-label">Current Period</span>
              <span className="stat-value">{formatNumber(analytics.currentPeriodVolume)} kg</span>
            </div>
            
            <div className="stat-card">
              <span className="stat-label">Previous Period</span>
              <span className="stat-value">{formatNumber(analytics.previousPeriodVolume)} kg</span>
            </div>
            
            <div className="stat-card">
              <span className="stat-label">Volume Change</span>
              {renderTrend(analytics.volumeChangePercentage)}
            </div>
          </div>

          {analytics.history?.length > 0 ? (
            <div className="chart-container">
              <div className="chart-bars-area">
                {analytics.history.map((point) => {
                  const pointVolume = point.totalVolume || 0
                  const pointLabel = point.periodStart 
                    ? formatDate(point.periodStart, { month: 'short', day: 'numeric' }) 
                    : ''

                  const heightPercentage = maxChartValue === 0 ? 0 : (pointVolume / maxChartValue) * 100
                  
                  return (
                    <div key={point.periodStart} className="chart-bar-group">
                      <div className="chart-bar-wrapper">
                        <div 
                          className="chart-bar-fill" 
                          style={{ height: `${heightPercentage}%` }}
                          title={`${pointLabel}: ${formatNumber(pointVolume)} kg`}
                        ></div>
                      </div>
                      <span className="chart-x-label">{pointLabel}</span>
                    </div>
                  )
                })}
              </div>
            </div>
          ) : (
            <div className="empty-state">
              <p>No training history available yet.</p>
            </div>
          )}
        </>
      )}
    </main>
  )
}

export default TrainingAnalytics