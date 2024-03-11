export interface Invoice {
  id: number;
  userId: string;
  balance: number;
  timestamp: number;
  previousInvoiceTimestamp: number;
  mailed: boolean;
  public: boolean;
  amounts: Amount[];
}

export interface Amount {
  itemId: string;
  amount: number;
}

export interface InvoicePage {
  content: Invoice[];
  totalPages: number;
  size: number;
  number: number;
}
