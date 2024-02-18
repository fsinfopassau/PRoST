export interface ShopHistoryEntry {
  id: number;
  userId: string;
  userDisplayname: string;
  itemId: string;
  price: number;
  timestamp: number;
}

export function convertUnixTimestampToTime(unixSeconds: number): string {
  const differenceInMilliseconds = new Date(unixSeconds * 1000).getTime();

  const secondsInMs = 1000;
  const minutesInMs = secondsInMs * 60;
  const hoursInMs = minutesInMs * 60;
  const daysInMs = hoursInMs * 24;

  const elapsedDays = Math.floor(differenceInMilliseconds / daysInMs);
  const elapsedHours = Math.floor(
    (differenceInMilliseconds % daysInMs) / hoursInMs
  );
  const elapsedMinutes = Math.floor(
    (differenceInMilliseconds % hoursInMs) / minutesInMs
  );
  const elapsedSeconds = Math.floor(
    (differenceInMilliseconds % minutesInMs) / secondsInMs
  );

  function formatTime(time: number): string {
    return time < 10 ? "0" + time : time.toString();
  }

  const formattedTime = `${formatTime(elapsedDays)} days, ${formatTime(
    elapsedHours
  )}:${formatTime(elapsedMinutes)}:${formatTime(elapsedSeconds)}`;

  return formattedTime;
}

export function getTimeSince(unixSeconds: number): string {
  const previousDate = new Date(unixSeconds * 1000);

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
