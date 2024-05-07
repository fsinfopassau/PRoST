export interface Transaction {
  id: number;
  bearerId: string;
  senderId: string;
  receiverId: string;
  transactionType: TransactionType;
  previous: number;
  amount: number;
  timestamp: number;
}

export interface TransactionPage {
  content: Transaction[];
  totalPages: number;
  size: number;
  number: number;
}

export enum TransactionType {
  DEPOSIT,
  BUY,
  REFUND,
  CHANGE,
}
