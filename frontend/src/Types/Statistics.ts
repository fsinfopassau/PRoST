import { ShopItem } from "./ShopItem";
import { User } from "./User";

export enum TimeSpan {
    WEEK = "WEEK",
    MONTH = "MONTH",
    ALL_TIME = "ALL_TIME",
}

// backend calculated
export enum ItemLeaderboardType {
    TOP_SELLING_ITEMS = "TOP_SELLING_ITEMS",
    ITEM_REVENUE = "ITEM_REVENUE",
}
export enum UserLeaderboardType {
    // frontend calculated
    DEBT_CUSTOMER = "DEBT_CUSTOMER",
    MVP = "MVP",
    // backend calculated
    LOYAL_CUSTOMER = "LOYAL_CUSTOMER",
    LUXURY_CUSTOMER = "LUXURY_CUSTOMER",
    KIOSK_CUSTOMER = "KIOSK_CUSTOMER",
}
export enum CompositeLeaderboardType {
    ITEM_USER = "ITEM_USER",
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

export interface LeaderboardCompositeEntry {
    key1: string;
    key1DisplayName: string;
    key2: string;
    key2DisplayName: string;
    value: number;
}

export function toTimeSpan(value: string): TimeSpan | undefined {
    if (Object.values(TimeSpan).includes(value as TimeSpan)) {
        return value as TimeSpan;
    }
    return undefined; // Return undefined if no match is found
}

export const convertUsersBalance = (users: User[]): LeaderboardUserEntry[] => {
    return users.filter((u) => {
        return u.enabled
    }).map((user) => ({
        key: user.id,
        entity: user,
        value: user.balance,
    }))
};

export const convertUsersSpent = (users: User[]): LeaderboardUserEntry[] => {
    return users.filter((u) => {
        return u.enabled
    }).map((user) => ({
        key: user.id,
        entity: user,
        value: user.totalSpent,
    }))
};