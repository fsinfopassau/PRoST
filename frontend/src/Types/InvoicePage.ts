import { Invoice } from "./Invoice";

export interface InvoicePage {
    content: Invoice[];
    totalPages: number;
    size: number;
    number: number;
}