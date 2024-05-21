import { ShopHistory } from "../HistoryTab/ShopHistory";
import { TransactionHistory } from "../HistoryTab/TransactionHistory";

export function PersonalHistoryView() {
  return (
    <>
      <div className="SingleCardContainer">
        <div style={{ width: "100rem" }}>
          <ShopHistory personal={true} />
          <TransactionHistory personal={true} />
        </div>
      </div>
    </>
  );
}
