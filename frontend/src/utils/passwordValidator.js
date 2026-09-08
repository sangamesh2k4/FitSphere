export const PASSWORD_REGEX =
  /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&^#()_\-+=])[A-Za-z\d@$!%*?&^#()_\-+=]{8,}$/;

export const validatePassword = (password) => {
  if (password.length < 8) {
    return "Password must be at least 8 characters long.";
  }
  if (!/[A-Z]/.test(password)) {
    return "Password must contain at least one uppercase letter.";
  }
  if (!/\d/.test(password)) {
    return "Password must contain at least one number.";
  }
  if (!/[@$!%*?&^#()_\-+=]/.test(password)) {
    return "Password must contain at least one special character.";
  }
  return null;
};

// ... keep your existing PASSWORD_REGEX and validatePassword functions above ...

// Add this new function for the real-time UI
export const getPasswordStrength = (password) => {
  const p = password || '';
  return {
    length: p.length >= 8,
    uppercase: /[A-Z]/.test(p),
    number: /\d/.test(p),
    special: /[@$!%*?&^#()_\-+=]/.test(p),
  };
};