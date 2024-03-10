export interface Invoice {
    id: number;
    userId: string;
    balance: number;
    timestamp: number;
    previousInvoiceTimestamp: number;
    shouldMail: boolean;
    isMailed: boolean;
    amounts: Map<string, number>;
}