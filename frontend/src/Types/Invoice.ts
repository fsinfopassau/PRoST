export interface Invoice {
  id: number;
  userId: string;
  userDisplayName: string;
  balance: number;
  timestamp: number;
  previousInvoiceTimestamp: number;
  mailed: boolean;
  published: boolean;
  amounts: Amount[];
}

export interface Amount {
  itemId: string;
  amount: number;
  singeItemPrice: number;
}

export interface InvoicePage {
  content: Invoice[];
  totalPages: number;
  size: number;
  number: number;
}
