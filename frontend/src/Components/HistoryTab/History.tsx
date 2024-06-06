import { ShopHistory } from "./ShopHistory";
import { TransactionHistory } from "./TransactionHistory";

export function History() {
  return (
    <>
      <div className="SingleCardContainer">
        <div>
          <ShopHistory personal={false} />
          <TransactionHistory personal={false} />
        </div>
      </div>
    </>
  );
}
