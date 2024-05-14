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

// more elaborate solution with more edgecases required
export function formatMoneyInput(money: string): string {
  return money.replace(",", ".");
}
