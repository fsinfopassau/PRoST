import { ShopItem } from "./ShopItem";
import { User } from "./User";

export enum TimeSpan {
    MONTH = "MONTH",
    ALL_TIME = "ALL_TIME",
}

export enum ItemLeaderboardType {
    TOP_SELLING_ITEMS = "TOP_SELLING_ITEMS"
}
export enum UserLeaderboardType {
    LOYAL_CUSTOMER = "LOYAL_CUSTOMER",
    LUXURY_CUSTOMER = "LUXURY_CUSTOMER"
}

export interface LeaderboardUserEntry {
    key: string;
    entity: User;
    value: number;
}

export interface LeaderboardItemEntry {
    key: string;
    entity: ShopItem;
    value: number;
}

export function toTimeSpan(value: string): TimeSpan | undefined {
    if (Object.values(TimeSpan).includes(value as TimeSpan)) {
        return value as TimeSpan;
    }
    return undefined; // Return undefined if no match is found
}