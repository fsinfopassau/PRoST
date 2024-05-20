
export function convertTimestampToTime(unixTimestamp: number): string {
  // Convert Unix timestamp to Date object
  const date = new Date(unixTimestamp);

  // Define options for formatting
  const options: Intl.DateTimeFormatOptions = {
    day: "numeric",
    month: "2-digit",
    year: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  };

  // Format date and time
  const formattedDate = date.toLocaleDateString("de-DE", options);

  // Return formatted date and time
  return formattedDate;
}

export function getTimeSince(unixMillis: number): string {
  const previousDate = new Date(unixMillis);

  // Current date
  const currentDate: Date = new Date();

  const milliDiff: number = currentDate.getTime() - previousDate.getTime();

  const totalSeconds = Math.floor(milliDiff / 1000);
  const totalMinutes = Math.floor(totalSeconds / 60);
  const totalHours = Math.floor(totalMinutes / 60);
  const totalDays = Math.floor(totalHours / 24);

  // Getting the time left
  const remSeconds = totalSeconds % 60;
  const remMinutes = totalMinutes % 60;
  const remHours = totalHours % 24;

  if (totalDays) {
    return `${totalDays}d ago`;
  } else if (remHours) {
    return `${remHours}h ago`;
  } else if (remMinutes) {
    return `${remMinutes}m ago`;
  } else if (remSeconds) {
    return `${remSeconds}s ago`;
  }

  return `now`;
}

export function formatMoney(
  balance: number | undefined,
  decimalCount = 2
): string {
  if (!balance && balance !== 0) {
    return "";
  }
  const formatted = new Intl.NumberFormat("de-DE", {
    minimumFractionDigits: decimalCount,
    maximumFractionDigits: decimalCount,
  }).format(balance);

  return formatted + " €";
}


/* DataValidation */

const EMAIL_PATTERN: string = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.+[a-zA-Z0-9.-]+$";
const MAX_NAME_LENGTH: number = 30;

export function isValidString(value: string): boolean {
  if (!value || value.trim() === "") {
    return false;
  }
  if (value.length > MAX_NAME_LENGTH) {
    return false;
  }
  return true;
}

/**
 * Filters all kind of ids that have to be readable to be lowercase and without spaces
 *
 * @param id the previous id
 * @returns the filtered id
 */
export function filterNameId(id: string): string {
  let filtered: string = "";
  for (const c of id.toLowerCase()) {
    if (c.toLowerCase() === c || /\d/.test(c)) {
      filtered += c;
    }
  }
  return filtered;
}

export function isValidEmail(email: string): boolean {
  if (!email || !isValidString(email)) {
    return false;
  }
  const pattern: RegExp = new RegExp(EMAIL_PATTERN);
  return pattern.test(email);
}

export function getValidMoney(value: string): number | undefined {
  if (!value.trim()) {
    return undefined;
  }

  const numberValue = parseFloat(value.replace(",", ".").replace(/[^0-9.-]+/g, ''));
  if (isNaN(numberValue)) {
    return undefined;
  }

  // Check if the number has more than 2 decimal places
  const decimalPlaces = (numberValue.toString().split('.')[1] || '').length;
  if (decimalPlaces > 2) {
    return undefined;
  }

  return parseFloat(numberValue.toFixed(2));
}