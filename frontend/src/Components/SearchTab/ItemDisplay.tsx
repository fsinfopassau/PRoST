import { useNavigate, useParams } from "react-router-dom";
import { ShopItem } from "../../Types/ShopItem";
import { AspectRatio } from "@radix-ui/react-aspect-ratio";

export function ItemDisplay(props: { item: ShopItem }) {
  const navigate = useNavigate();
  const { userId } = useParams();

  function selectItem() {
    navigate(`/shop/${userId}/${props.item.id}`);
  }

  return (
    <>
      <div className="DisplayCard" onClick={selectItem}>
        <AspectRatio ratio={1 / 1}>
          <img
              className="Image"
              src="/Beer.jpg"
              alt="Landscape photograph by Tobias Tullius"
              width={"100%"}
            />
        </AspectRatio>
        {props.item.displayName}
      </div>
    </>
  );
}
