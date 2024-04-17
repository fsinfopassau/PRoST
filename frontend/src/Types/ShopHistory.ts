import { Transaction } from "./TransactionEntry";

export interface ShopHistoryEntry {
  id: number;
  userId: string;
  userDisplayName: string;
  itemId: string;
  itemDisplayName: string;
  price: number;
  amount: number;
  timestamp: number;
  transaction: Transaction;
  refundTransaction: Transaction;
}
