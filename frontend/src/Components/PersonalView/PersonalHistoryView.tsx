import { ShopHistory } from "../HistoryTab/ShopHistory";
import { TransactionHistory } from "../HistoryTab/TransactionHistory";

export function PersonalHistoryView() {
  return (
    <>
      <div className="SingleCardContainer">
        <ShopHistory personal={true} />
        <TransactionHistory personal={true} />
      </div>
    </>
  );
}
