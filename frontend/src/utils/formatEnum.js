export function formatEnum(value) {
  if (!value) return ''

  return value
    .replaceAll('_', ' ')
    .toLowerCase()
    .replace(/\b\w/g, letter => letter.toUpperCase())
}