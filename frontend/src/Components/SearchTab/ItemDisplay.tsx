import { useNavigate, useParams } from "react-router-dom";
import { ShopItem } from "../../Types/ShopItem";
import { AspectRatio } from "@radix-ui/react-aspect-ratio";
import { useEffect, useState } from "react";
import { getItemDisplayPicture } from "../Util/Queries";

export function ItemDisplay(props: { item: ShopItem }) {
  const { item } = props;
  const navigate = useNavigate();
  const { userId } = useParams();
  const [imageUrl, setImageUrl] = useState<string>("/Beer.jpg");

  function selectItem() {
    navigate(`/shop/${userId}/${props.item.id}`);
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
      <div className="DisplayCard" onClick={selectItem}>
        <AspectRatio ratio={1 / 1}>
          <img
            className="Image"
            src={imageUrl}
            alt="Item Image"
            width={"100%"}
          />
        </AspectRatio>
        {props.item.displayName}
      </div>
    </>
  );
}
