import { getPasswordStrength } from '../../utils/passwordValidator';
import '../../css/PasswordRequirements.css';

function PasswordRequirements({ password }) {
  const criteria = getPasswordStrength(password);
  
  // Don't show the scary red text before they even start typing
  const isTyping = password.length > 0;

  const getClassName = (isValid) => {
    if (!isTyping) return 'requirement-neutral';
    return isValid ? 'requirement-met' : 'requirement-unmet';
  };

  const getIcon = (isValid) => {
    if (!isTyping) return '⚪'; // Neutral dot
    return isValid ? '✅' : '❌';
  };

  return (
    <ul className="password-requirements-list">
      <li className={getClassName(criteria.length)}>
        {getIcon(criteria.length)} At least 8 characters
      </li>
      <li className={getClassName(criteria.uppercase)}>
        {getIcon(criteria.uppercase)} At least 1 uppercase letter
      </li>
      <li className={getClassName(criteria.number)}>
        {getIcon(criteria.number)} At least 1 number
      </li>
      <li className={getClassName(criteria.special)}>
        {getIcon(criteria.special)} At least 1 special character
      </li>
    </ul>
  );
}

export default PasswordRequirements;