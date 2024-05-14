import { useEffect, useState } from "react";
import { createNewShopItem, getAllShopItems } from "../../Queries";
import { ShopItem } from "../../Types/ShopItem";
import { ItemSettingCard } from "./ItemSettingCard";
import { Separator } from "@radix-ui/react-separator";
import ScrollDialog from "../Util/ScrollDialog";
import { PlusCircledIcon } from "@radix-ui/react-icons";
import { toast } from "react-toastify";
import Fuse from "fuse.js";
import { formatMoneyInput } from "../../Format";

export function ItemSettings() {
  const [searchValue, setSearchValue] = useState("");
  const [items, setItems] = useState<ShopItem[]>([]);
  const [newId, setNewId] = useState<string>("");
  const [newCategory, setNewCategory] = useState<string>("");
  const [newDisplayName, setNewDisplayName] = useState<string>("");
  const [newPrice, setNewPrice] = useState<number>(0);

  useEffect(reloadShopItems, []);

  function reloadShopItems() {
    getAllShopItems().then((itemList) => {
      if (itemList) setItems(itemList);
    });
  }

  function createShopItem() {
    const item: ShopItem = {
      id: newId,
      category: newCategory,
      displayName: newDisplayName,
      price: newPrice,
      enabled: true,
    };

    createNewShopItem(item).then((success) => {
      if (success) {
        toast.success("Item" + newId + " created");
        reloadShopItems();
      } else {
        toast.error("Master, you failed me!");
      }
    });
  }

  const handleIdChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNewId(event.target.value);
  };

  const handleCategoryChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNewCategory(event.target.value);
  };

  const handleDisplayNameChange = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setNewDisplayName(event.target.value);
  };

  const handlePriceChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNewPrice(formatMoneyInput(event.target.value));
  };

  function filter(items: ShopItem[]): ShopItem[] {
    if (searchValue.trim().length === 0) {
      return items;
    }

    const fuse = new Fuse(items, {
      keys: ["id", "displayName"],
    });
    return fuse.search(searchValue).map((result) => result.item);
  }

  return (
    <div className="DisplayCard">
      <div style={{ display: "flex", gap: "1rem" }}>
        <input
          type="text"
          placeholder="Item"
          className="Input"
          onChange={(e) => setSearchValue(e.target.value)}
        ></input>
        <ScrollDialog
          title="Neues Item"
          trigger={
            <div className="Button icon green">
              <div className="green">
                <PlusCircledIcon />
              </div>
            </div>
          }
          onSubmit={createShopItem}
        >
          <div
            style={{
              padding: "1rem",
              display: "flex",
              flexDirection: "column",
              gap: ".5rem",
            }}
          >
            <div className="DialogDescription">
              Identifier:
              <fieldset className="Fieldset">
                <input
                  className="Input"
                  onChange={handleIdChange}
                  placeholder="vergammelte Milch"
                ></input>
              </fieldset>
            </div>
            <div className="DialogDescription">
              Kategorie:
              <fieldset className="Fieldset">
                <input
                  className="Input"
                  onChange={handleCategoryChange}
                  placeholder="abgestandene Getränke"
                ></input>
              </fieldset>
            </div>
            <div className="DialogDescription">
              Name:
              <fieldset className="Fieldset">
                <input
                  className="Input"
                  onChange={handleDisplayNameChange}
                  placeholder="Restbier von gestern"
                ></input>
              </fieldset>
            </div>
            <div className="DialogDescription">
              Preis:
              <fieldset className="Fieldset">
                <input
                  className="Input"
                  onChange={handlePriceChange}
                  placeholder="12,34"
                ></input>
              </fieldset>
            </div>
          </div>
        </ScrollDialog>
      </div>
      <Separator className="Separator" />
      <div className="GridContainer">
        {filter(items).map((item, index) => (
          <ItemSettingCard item={item} key={index} onUpdate={reloadShopItems} />
        ))}
      </div>
    </div>
  );
}
