export interface Invoice {
    id: number;
    userId: string;
    balance: number;
    timestamp: number;
    previousInvoiceTimestamp: number;
    mailed: boolean;
    amounts: Map<string, number>;
}