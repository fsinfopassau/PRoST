import { useNavigate, useParams } from "react-router-dom";
import { ShopItem } from "../../Types/ShopItem";

export function ItemDisplay(props: { item: ShopItem }) {
  const navigate = useNavigate();
  const { userid } = useParams();

  function selectItem() {
    navigate(`/shop/${userid}/${props.item.id}`);
  }

  return (
    <>
      <button className="Button" onClick={selectItem}>
        {props.item.displayName}
      </button>
    </>
  );
}
