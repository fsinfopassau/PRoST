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
import { changeShopItem, deleteShopItem, enableItem } from "../Util/Queries";
import { AspectRatio } from "@radix-ui/react-aspect-ratio";
import { useState } from "react";
import { uploadItemDisplayPicture } from "../Util/FileUploadService";
import { ButtonDialogChanger } from "../Util/ButtonDialogChange";

export function ItemSettingCard(props: {
  item: ShopItem;
  onUpdate: () => void;
}) {
  const { item, onUpdate } = props;
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      setSelectedFile(event.target.files[0]);

      if (selectedFile) {
        console.log("upload " + selectedFile.name);
        uploadItemDisplayPicture(item, selectedFile);
      }
    }
  };

  function setImage() {
    document.getElementById("filekevin")?.click();
  }

  function setPrice(newPrice: string) {
    console.log("Price = " + newPrice);
    changeShopItem(item, newPrice, "price").then((result) => {
      if (result) {
        onUpdate();
      }
    });
  }

  function setName(newName: string) {
    changeShopItem(item, newName, "displayname").then((result) => {
      if (result) {
        onUpdate();
      }
    });
  }

  function deleteItem() {
    deleteShopItem(item).then((result) => {
      if (result) {
        onUpdate();
      }
    });
  }

  function setCategory(newCategory: string) {
    changeShopItem(item, newCategory, "category").then((result) => {
      if (result) {
        onUpdate();
      }
    });
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
          <div className="Button">
            <Pencil2Icon />
            <ButtonDialogChanger
              name={item.displayName}
              dialogName="Namen ändern"
              dialogDesc="Ändere hier den Namen des Items"
              onSubmit={setName}
              key={"NameChanger"}
            />
          </div>
          <div className="Button">
            <BookmarkIcon />
            <ButtonDialogChanger
              name={item.category}
              dialogName="Kategorie ändern"
              dialogDesc="Ändere hier die Kategorie des Items"
              onSubmit={setCategory}
              key={"CategoryChanger"}
            />
          </div>
          <div className="Button">
            <LightningBoltIcon />
            <ButtonDialogChanger
              name={formatMoney(item.price)}
              dialogName="Preis ändern"
              dialogDesc="Ändere hier den Preis des Items"
              onSubmit={setPrice}
              key={"PriceChanger"}
            />
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
            <input
              type="file"
              onChange={handleFileChange}
              id="filekevin"
              style={{ display: "none" }}
            ></input>
          </AspectRatio>
        </div>
      </div>
    </div>
  );
}
