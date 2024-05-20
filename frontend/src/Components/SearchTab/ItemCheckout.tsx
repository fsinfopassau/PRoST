import { useNavigate, useParams } from "react-router-dom";
import { buyItem, getItemDisplayPicture, getShopItem } from "../../Queries";
import { ShopItem } from "../../Types/ShopItem";
import { useEffect, useState } from "react";
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

export function ItemCheckout() {
  const { userId, itemId } = useParams();
  const [item, setItem] = useState<ShopItem>();
  const [imageUrl, setImageUrl] = useState<string>(`${BASE_PATH}/img/Beer.jpg`);
  const [amount, setAmount] = useState<number>(1);
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
    setAmount(Math.min(amount + 1, 10));
  }
  function decrement() {
    setAmount(Math.max(amount - 1, 1));
  }

  function checkout() {
    if (userId && itemId) {
      console.log("checkout", userId, itemId);
      buyItem(userId, itemId, amount).then((result) => {
        if (result) {
          toast(amount + "x " + item?.displayName + " gekauft!");
        } else {
          toast.error(item?.displayName + " konnte nicht gekauft werden!");
        }
        navigate("/");
      });
    }
  }

  function getPrice(amount: number): string {
    if (item) {
      return formatMoney(item.price * amount);
    }
    return formatMoney(0);
  }

  return (
    <>
      <div id="Checkout">
        <div id="Checkout-Item">
          <h2>{item?.displayName}</h2>
          <img
            className="Image"
            src={imageUrl}
            alt="Item Image"
            style={{ width: "100%" }}
          />
          <div className="SpreadContainer">
            <div style={{ display: "flex", alignItems: "center" }}>
              <BookmarkIcon />
              {item?.category}
            </div>
            <div
              className="bold"
              style={{ display: "flex", alignItems: "center" }}
            >
              <LightningBoltIcon />
              {getPrice(amount)}
            </div>
          </div>
        </div>
        <div className="CheckoutCounter">
          <button className="Button" onClick={decrement}>
            <ChevronLeftIcon height={60} width={60} />
          </button>
          <div>
            <div>{amount}</div>
          </div>
          <button className="Button" onClick={increment}>
            <ChevronRightIcon height={60} width={60} />
          </button>
        </div>
        <button id="Checkout-Complete" className="Button" onClick={checkout}>
          <PaperPlaneIcon width={50} height={35} />
        </button>
      </div>
    </>
  );
}
