import { ShopHistory } from "./ShopHistory";
import { TransactionHistory } from "./TransactionHistory";

export function History() {
  return (
    <>
      <div className="SingleCardContainer">
        <ShopHistory personal={false} />
        <TransactionHistory personal={false} />
      </div>
    </>
  );
}
