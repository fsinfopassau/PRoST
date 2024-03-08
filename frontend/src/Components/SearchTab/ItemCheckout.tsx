import { useNavigate, useParams } from "react-router-dom";
import { buyItem, getItemDisplayPicture, getShopItem } from "../Util/Queries";
import { ShopItem } from "../../Types/ShopItem";
import { useEffect, useState } from "react";
import { formatMoney } from "../../Types/User";
import { toast } from "react-toastify";
import {
  BookmarkIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
  LightningBoltIcon,
  PaperPlaneIcon,
} from "@radix-ui/react-icons";

export function ItemCheckout() {
  const { userId, itemId } = useParams();
  const [item, setItem] = useState<ShopItem>();
  const [imageUrl, setImageUrl] = useState<string>("/Beer.jpg");
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
    console.log("checkout", userId, itemId);
    if (userId && itemId) {
      buyItem(userId, itemId, amount).then((result) => {
        if (result) {
          toast(amount + "x " + item?.displayName + " gekauft!");
        } else {
          toast.error(item?.displayName + " konnte nicht gekauft werden!");
        }
      });
    }
    navigate("/");
  }

  function getPrice(amount: number): string {
    if (item) {
      return formatMoney(item.price * amount);
    }
    return formatMoney(0);
  }

  return (
    <>
      <h2>{item?.displayName}</h2>
      <div style={{ width: "100%", display: "flex", justifyContent: "center" }}>
        <div>
          <img
            className="Image"
            src={imageUrl}
            alt="Item Image"
            width={"100%"}
            style={{ maxWidth: "20rem" }}
          />
          <div className="SpreadContainer" style={{ fontSize: "1.5rem" }}>
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
      </div>
      <div id="CheckoutBar">
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
        <button className="Button" onClick={checkout}>
          <PaperPlaneIcon width={50} height={35} />
        </button>
      </div>
    </>
  );
}
