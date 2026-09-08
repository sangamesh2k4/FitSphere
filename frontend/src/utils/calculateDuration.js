export function calculateDuration(startedAt, completedAt) {
  if (!startedAt || !completedAt) {
    return 'Unknown'
  }

  const start = new Date(startedAt)
  const end = new Date(completedAt)

  const totalMinutes = Math.round((end - start) / 60000)

  const hours = Math.floor(totalMinutes / 60)
  const minutes = totalMinutes % 60

  if (hours > 0) {
    return `${hours}h ${minutes}m`
  }

  return `${minutes} min`
}