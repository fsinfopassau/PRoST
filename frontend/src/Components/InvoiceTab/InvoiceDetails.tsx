import { Link } from "react-router-dom";
import { Amount, Invoice } from "../../Types/Invoice";
import { convertTimestampToTime, formatMoney } from "../../Format";
import { useEffect, useState } from "react";
import { ShopItem } from "../../Types/ShopItem";
import { getAllShopItems } from "../../Queries";
import {
  DoubleArrowRightIcon,
  ThickArrowRightIcon,
} from "@radix-ui/react-icons";

export function InvoiceDetails(props: { invoice: Invoice }) {
  const { invoice } = props;
  const [items, setItems] = useState<ShopItem[]>([]);

  useEffect(() => {
    getAllShopItems().then((itemList) => {
      if (itemList) {
        setItems(itemList);
      }
    });
  }, []);

  function getPrice(amount: Amount): string {
    return formatMoney(-amount.amount * amount.singeItemPrice);
  }

  function getItem(amount: Amount): ShopItem | undefined {
    return items.find((i) => {
      if (i.id === amount.itemId) return i;
    });
  }

  return (
    <div>
      <h3>
        <Link to={`/stats/users/${invoice.userId}`} className="bold">
          {invoice.userDisplayName} ({invoice.userId})
        </Link>
      </h3>
      <div style={{ margin: "1.5rem 0" }}>
        <table className="Table">
          <tbody>
            {invoice.amounts?.map((amount, index) => (
              <tr key={amount.itemId + "" + index} className="table-entry">
                <th className="left name bold">
                  {getItem(amount)
                    ? getItem(amount)?.displayName
                    : amount.itemId}
                </th>
                <th className="amount">{amount.amount}</th>
                <th className="icon">x</th>
                <th className="balance ">{formatMoney(amount.singeItemPrice)}</th>
                <th className="icon">
                  <div>
                    <ThickArrowRightIcon />
                  </div>
                </th>
                <th className="balance bold right">{getPrice(amount)}</th>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div
        style={{
          display: "flex",
          alignContent: "space-around",
          padding: "0 0.7rem ",
        }}
      >
        <div style={{ display: "flex", flexGrow: "2" }}>
          {invoice.previousInvoiceTimestamp === 0 ? (
            <></>
          ) : (
            <>
              <div>
                {convertTimestampToTime(invoice.previousInvoiceTimestamp)}{" "}
              </div>
              <div className="icon">
                <div>
                  <DoubleArrowRightIcon />
                </div>
              </div>
            </>
          )}
          <div>{convertTimestampToTime(invoice.timestamp)}</div>
        </div>
        <div
          className="bold"
          style={{ alignContent: "flex-end", flexGrow: "0" }}
        >
          {formatMoney(invoice.balance)}
        </div>
      </div>
    </div>
  );
}
