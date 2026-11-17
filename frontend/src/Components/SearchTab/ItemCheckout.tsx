import { useNavigate, useParams } from "react-router-dom";
import { buyItem, getItemDisplayPicture, getShopItem, newRequestId } from "../../Queries";
import { ShopItem } from "../../Types/ShopItem";
import { useEffect, useRef, useState } from "react";
import { toast } from "react-toastify";
import {
  BookmarkIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
  LightningBoltIcon,
  PaperPlaneIcon,
} from "@radix-ui/react-icons";
import { formatMoney } from "../../Format";
import { BASE_PATH } from "../App";
import { ErrorComponent } from "../Util/ErrorTab";

export function ItemCheckout() {
  const { userId, itemId } = useParams();
  const [item, setItem] = useState<ShopItem>();
  const [imageUrl, setImageUrl] = useState<string>(`${BASE_PATH}/img/Beer.jpg`);
  const [amount, setAmount] = useState<number>(1);
  const [buying, setBuying] = useState<boolean>(false);
  const buyingRef = useRef(false); // ref: blocks a second tap before the next render
  // One id per purchase. Every attempt of the same purchase (automatic retry after a lost
  // response, or the customer tapping again) uses it, so the backend charges it only once.
  const requestId = useRef<string>(newRequestId());
  const navigate = useNavigate();

  useEffect(() => {
    if (itemId) {
      getShopItem(itemId).then((newItem) => {
        setItem(newItem);
      });
    }
  }, []);

  useEffect(() => {
    if (item) {
      getItemDisplayPicture(item).then((image) => {
        if (image) {
          setImageUrl(image);
        }
      });
    }
  }, [item]);

  function increment() {
    changeAmount(Math.min(amount + 1, 10));
  }

  function decrement() {
    changeAmount(Math.max(amount - 1, 1));
  }

  function changeAmount(newAmount: number) {
    if (newAmount !== amount) {
      requestId.current = newRequestId(); // different order = different purchase
      setAmount(newAmount);
    }
  }

  const RETRY_DELAYS_MS = [1000, 2000, 4000];

  /** Buys and, if the outcome is unknown (lost response), retries the SAME request. */
  async function buyWithRetry(userId: string, itemId: string): Promise<number> {
    let result = await buyItem(userId, itemId, amount, requestId.current);
    for (const delay of RETRY_DELAYS_MS) {
      if (result !== 3) {
        return result;
      }
      toast.info("Verbindung unterbrochen – versuche es erneut …", { toastId: "buy-retry" });
      await new Promise((resolve) => setTimeout(resolve, delay));
      result = await buyItem(userId, itemId, amount, requestId.current);
      if (result === 1) {
        // cooldown from our own first attempt that is still being processed: ask again
        result = 3;
      }
    }
    return result;
  }

  function checkout() {
    if (!userId || !itemId || buyingRef.current) {
      return;
    }
    buyingRef.current = true;
    setBuying(true);

    buyWithRetry(userId, itemId)
      .then((result) => {
        toast.dismiss("buy-retry");
        switch (result) {
          case -1:
            toast.error(item?.displayName + " konnte nicht gekauft werden!");
            break;
          case 0:
            toast(amount + "x " + item?.displayName + " gekauft!");
            navigate("/");
            break;
          case 1:
            toast.info("Warte etwas, um nochmal zu kaufen.");
            break;
          case 2:
            toast.warning("Ich bin ne 🫖, du Keck! 🙃");
            break;
          case 3:
            // requestId is kept: tapping again later cannot charge twice
            toast.error(
              "Keine Verbindung – unklar, ob der Kauf geklappt hat. Bitte im Verlauf prüfen. " +
                "Nochmal tippen bucht nicht doppelt."
            );
            break;
        }
      })
      .catch(() => {
        toast.error("Transaktionsfehler. Unerwarteter Anzeigefehler!");
      })
      .finally(() => {
        buyingRef.current = false;
        setBuying(false);
      });
  }

  function getPrice(amount: number): string {
    if (item) {
      return formatMoney(item.price * amount);
    }
    return formatMoney(0);
  }

  if (item) {
    return (
      <>
        <div id="Checkout">
          <div id="Checkout-Item">
            <h2>{item?.displayName}</h2>
            <img className="Image" src={imageUrl} alt="Item Image" style={{ width: "100%" }} />
            <div className="SpreadContainer">
              <div style={{ display: "flex", alignItems: "center" }}>
                <BookmarkIcon />
                {item?.category}
              </div>
              <div className="bold" style={{ display: "flex", alignItems: "center" }}>
                <LightningBoltIcon />
                {getPrice(amount)}
              </div>
            </div>
          </div>
          <div className="CheckoutCounter">
            <button className="Button icon" onClick={decrement}>
              <ChevronLeftIcon height={60} width={60} />
            </button>
            <div>
              <div>{amount}</div>
            </div>
            <button className="Button icon" onClick={increment}>
              <ChevronRightIcon height={60} width={60} />
            </button>
          </div>
          <button id="Checkout-Complete" className="Button" onClick={checkout} disabled={buying}>
            <PaperPlaneIcon width={50} height={35} />
          </button>
        </div>
      </>
    );
  } else {
    return <ErrorComponent />;
  }
}
