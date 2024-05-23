import { useEffect, useState } from "react";
import { createNewShopItem, getAllShopItems } from "../../Queries";
import { ShopItem } from "../../Types/ShopItem";
import { ItemSettingCard } from "./ItemSettingCard";
import { Separator } from "@radix-ui/react-separator";
import ScrollDialog from "../Util/ScrollDialog";
import { PlusCircledIcon } from "@radix-ui/react-icons";
import { toast } from "react-toastify";
import Fuse from "fuse.js";
import { filterNameId, getValidMoney, isValidString } from "../../Format";

export function ItemSettings() {
  const [searchValue, setSearchValue] = useState("");
  const [items, setItems] = useState<ShopItem[]>([]);
  const [newId, setNewId] = useState<string>("");
  const [newCategory, setNewCategory] = useState<string>("");
  const [newDisplayName, setNewDisplayName] = useState<string>("");
  const [newPrice, setNewPrice] = useState<number | undefined>(0);

  useEffect(reloadShopItems, []);

  function reloadShopItems() {
    getAllShopItems().then((itemList) => {
      if (itemList) setItems(itemList);
    });
  }

  function createShopItem() {
    if (newPrice) {

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
    const money = getValidMoney(event.target.value);
    if (money)
      setNewPrice(money);
    else {
      setNewPrice(undefined);
    }
  };

  function filter(items: ShopItem[]): ShopItem[] {
    if (searchValue.trim().length === 0) {
      return items;
    }

    const fuse = new Fuse(items, {
      keys: ["id", "displayName", "category"],
    });
    return fuse.search(searchValue).map((result) => result.item);
  }

  return (
    <div className="DisplayCard">
      <h2>Gegenstände</h2>
      <div className="tableSearch">
        <input
          type="text"
          placeholder="Id, Name, Kategorie"
          className="Input"
          onChange={(e) => setSearchValue(e.target.value)}
        ></input>
        <ScrollDialog
          title="Neuer Gegenstand"
          trigger={
            <div className="Button good-color icon">
              <div className="good-color">
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
                  className={isValidString(filterNameId(newId)) ? "Input good-color" : "Input danger-color"}
                  onChange={handleIdChange}
                  placeholder="vergammelte Milch"
                ></input>
              </fieldset>
            </div>
            <div className="DialogDescription">
              Kategorie:
              <fieldset className="Fieldset">
                <input
                  className={isValidString(newCategory) ? "Input good-color" : "Input danger-color"}
                  onChange={handleCategoryChange}
                  placeholder="abgestandene Getränke"
                ></input>
              </fieldset>
            </div>
            <div className="DialogDescription">
              Name:
              <fieldset className="Fieldset">
                <input
                  className={isValidString(newDisplayName) ? "Input good-color" : "Input danger-color"}
                  onChange={handleDisplayNameChange}
                  placeholder="Restbier von gestern"
                ></input>
              </fieldset>
            </div>
            <div className="DialogDescription">
              Preis:
              <fieldset className="Fieldset">
                <input
                  className={newPrice !== undefined ? "Input good-color" : "Input danger-color"}
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
