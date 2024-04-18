import { ShopHistory } from "../HistoryTab/ShopHistory";
import { TransactionHistory } from "../HistoryTab/TransactionHistory";

export function PersonalHistoryView() {
  return (
    <>
      <ShopHistory personal={true} />
      <TransactionHistory personal={true} />
    </>
  );
}
