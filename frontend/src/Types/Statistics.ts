import { User } from "./User";

export enum UserLeaderboardType {
    LOYAL_CUSTOMER = "LOYAL_CUSTOMER",
    LUXURY_CUSTOMER = "LUXURY_CUSTOMER"
}

export enum TimeSpan {
    MONTH = "MONTH",
    ALL_TIME = "ALL_TIME",
}

export interface LeaderboardUserEntry {
    item: User;
    value: number;
}

export function toTimeSpan(value: string): TimeSpan | undefined {
    if (Object.values(TimeSpan).includes(value as TimeSpan)) {
        return value as TimeSpan;
    }
    return undefined; // Return undefined if no match is found
}