import { ShopHistory } from "./ShopHistory";
import { TransactionHistory } from "./TransactionHistory";

export function History() {
  return (
    <>
      <ShopHistory personal={false} />
      <TransactionHistory personal={false} />
    </>
  );
}
