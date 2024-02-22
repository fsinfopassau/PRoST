import {
  BookmarkIcon,
  Cross1Icon,
  LightningBoltIcon,
  Pencil2Icon,
} from "@radix-ui/react-icons";
import { ShopItem } from "../../Types/ShopItem";
import { formatMoney } from "../../Types/User";
import { Separator } from "@radix-ui/react-separator";
import { Switch, SwitchThumb } from "@radix-ui/react-switch";
import { enableItem } from "../Util/Queries";
import { AspectRatio } from "@radix-ui/react-aspect-ratio";

export function ItemSettingCard(props: { item: ShopItem }) {
  const { item } = props;

  function setImage() {
    console.log("TODO Image");
  }

  function setPrice() {
    console.log("TODO Price");
  }

  function setName() {
    console.log("TODO Name");
  }

  function deleteItem() {
    console.log("TODO Delete");
  }

  function setCategory() {
    console.log("TODO Category");
  }

  function toggleEnable() {
    const newVal = !item.enabled;
    enableItem(item, newVal).then((result) => {
      if (result) {
        item.enabled = newVal;
      }
    });
  }

  return (
    <div className="DisplayCard">
      <div className="SpreadContainer">
        <h3 className="SpreadContainer bold">
          {item.id}
          <Switch
            className="SwitchRoot"
            defaultChecked={item.enabled}
            onCheckedChange={toggleEnable}
          >
            <SwitchThumb className="SwitchThumb" />
          </Switch>
        </h3>

        <div className="Button" onClick={deleteItem}>
          <Cross1Icon />
        </div>
      </div>
      <Separator className="Separator" />
      <div className="SpreadContainer">
        <div className="SelectionContainer" style={{ width: "60%" }}>
          <div className="Button" onClick={setName}>
            <Pencil2Icon />
            {item.displayName}
          </div>
          <div className="Button" onClick={setCategory}>
            <BookmarkIcon />
            {item.category}
          </div>
          <div className="Button" onClick={setPrice}>
            <LightningBoltIcon />
            {formatMoney(item.price)}
          </div>
        </div>
        <div style={{ width: "40%" }}>
          <AspectRatio ratio={1 / 1} onClick={setImage}>
            <img
              className="Image"
              src="/Beer.jpg"
              alt="Landscape photograph by Tobias Tullius"
              width={"100%"}
            />
          </AspectRatio>
        </div>
      </div>
    </div>
  );
}
