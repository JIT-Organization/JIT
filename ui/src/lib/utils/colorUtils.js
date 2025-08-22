/**
 * Calculates luminance (brightness) of a hex color.
 * @param hex - A hex color like '#1976D2'
 * @returns number between 0 (black) and 1 (white)
 */
export function getLuminance(hex) {
  hex = hex.replace(/^#/, '');
  const r = parseInt(hex.slice(0, 2), 16);
  const g = parseInt(hex.slice(2, 4), 16);
  const b = parseInt(hex.slice(4, 6), 16);
  return (0.299 * r + 0.587 * g + 0.114 * b) / 255;
}

/**
 * Lightens or darkens a hex color based on the given factor.
 * @param hexColor - A hex color like '#1976D2'
 * @param factor - Between -1 (darken) and 1 (lighten)
 */
export function adjustBrightness(hexColor, factor) {
  hexColor = hexColor.replace(/^#/, '');
  let r = parseInt(hexColor.slice(0, 2), 16);
  let g = parseInt(hexColor.slice(2, 4), 16);
  let b = parseInt(hexColor.slice(4, 6), 16);

  r = Math.min(255, Math.max(0, Math.round(r + factor * (255 - r))));
  g = Math.min(255, Math.max(0, Math.round(g + factor * (255 - g))));
  b = Math.min(255, Math.max(0, Math.round(b + factor * (255 - b))));

  return `#${((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1).padStart(6, '0')}`;
}

/**
 * Returns a contrasting text color for a given background.
 * - If the background is light, returns black.
 * - If dark, returns a darker version of the color.
 */
export function contrastTextColor(hexColor) {
  const luminance = getLuminance(hexColor);
  return luminance > 0.8 ? '#000000' : adjustBrightness(hexColor, -0.5);
}

/**
 * Returns either black or white for best contrast against a background.
 */
export function blackOrWhiteTextColor(hexColor) {
  const luminance = getLuminance(hexColor);
  return luminance > 0.5 ? '#000000' : '#ffffff';
} 