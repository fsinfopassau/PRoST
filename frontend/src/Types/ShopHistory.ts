import { Transaction } from "./Transaction";

export interface ShopHistoryEntry {
  id: number;
  userId: string;
  userDisplayName: string;
  itemId: string;
  itemDisplayName: string;
  itemPrice: number;
  amount: number;
  timestamp: number;
  transaction: Transaction;
  refundTransaction: Transaction;
}
