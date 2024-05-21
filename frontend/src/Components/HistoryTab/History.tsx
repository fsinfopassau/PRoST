import { ShopHistory } from "./ShopHistory";
import { TransactionHistory } from "./TransactionHistory";

export function History() {
  return (
    <>
      <div className="SingleCardContainer">
        <div style={{ width: "100rem" }}>
          <ShopHistory personal={false} />
          <TransactionHistory personal={false} />
        </div>
      </div>
    </>
  );
}
