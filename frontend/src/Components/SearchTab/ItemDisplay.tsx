import { useNavigate, useParams } from "react-router-dom";
import { ShopItem } from "../../Types/ShopItem";

export function ItemDisplay(props: { item: ShopItem }) {
  const navigate = useNavigate();
  const { userId } = useParams();

  function selectItem() {
    navigate(`/shop/${userId}/${props.item.id}`);
  }

  return (
    <>
      <button className="Button" onClick={selectItem}>
        {props.item.displayName}
      </button>
    </>
  );
}
