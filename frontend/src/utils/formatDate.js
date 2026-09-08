export function formatDate(date, options) {
  if (!date) return 'Unknown'

  return new Date(date).toLocaleDateString(
    'en-US',
    options || {
      month: 'long',
      day: 'numeric',
      year: 'numeric'
    }
  )
}