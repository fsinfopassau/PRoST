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
  hidden: boolean;
}

export interface ShopHistoryEntryPage {
  content: ShopHistoryEntry[];
  totalPages: number;
  size: number;
  number: number;
}
