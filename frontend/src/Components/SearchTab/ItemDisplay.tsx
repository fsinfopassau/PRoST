import { useNavigate, useParams } from "react-router-dom";
import { ShopItem } from "../../Types/ShopItem";
import { AspectRatio } from "@radix-ui/react-aspect-ratio";
import { useEffect, useState } from "react";
import { getItemDisplayPicture } from "../../Queries";
import { formatMoney } from "../../Format";
import { BASE_PATH } from "../App";

export function ItemDisplay(props: { item: ShopItem }) {
  const { item } = props;
  const navigate = useNavigate();
  const { userId } = useParams();
  const [imageUrl, setImageUrl] = useState<string>(`${BASE_PATH}/img/Beer.jpg`);

  function selectItem() {
    navigate(`/shop/${userId}/${item.id}`);
  }

  useEffect(() => {
    getItemDisplayPicture(item).then((image) => {
      if (image) {
        setImageUrl(image);
      }
    });
  }, [item]);

  return (
    <>
      <div onClick={selectItem}>
        <AspectRatio ratio={1 / 1} className="AspectRatio">
          <img src={imageUrl} alt="Item Image" className="image-fitted" />
        </AspectRatio>
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          <div>{item.displayName}</div>
          <div className="bold">{formatMoney(item.price)}</div>
        </div>
      </div>
    </>
  );
}
