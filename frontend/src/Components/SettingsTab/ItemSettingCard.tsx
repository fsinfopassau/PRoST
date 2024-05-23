import {
  BookmarkIcon,
  CheckCircledIcon,
  Cross1Icon,
  CrossCircledIcon,
  LightningBoltIcon,
  Pencil2Icon,
} from "@radix-ui/react-icons";
import { ShopItem } from "../../Types/ShopItem";
import { Separator } from "@radix-ui/react-separator";
import { Switch, SwitchThumb } from "@radix-ui/react-switch";
import {
  changeShopItem,
  deleteShopItem,
  enableItem,
  getItemDisplayPicture,
  uploadItemDisplayPicture,
} from "../../Queries";
import { AspectRatio } from "@radix-ui/react-aspect-ratio";
import { useEffect, useRef, useState } from "react";

import { ButtonDialogChanger } from "../Util/ButtonDialogChange";
import { toast } from "react-toastify";
import { formatMoney } from "../../Format";
import ScrollDialog from "../Util/ScrollDialog";
import { BASE_PATH } from "../App";

export function ItemSettingCard(props: {
  item: ShopItem;
  onUpdate: () => void;
}) {
  const { item, onUpdate } = props;
  const [imageUrl, setImageUrl] = useState<string>(`${BASE_PATH}/img/Beer.jpg`);
  const fileInput = useRef<HTMLInputElement>(null);

  useEffect(() => {
    getItemDisplayPicture(item).then((image) => {
      if (image) {
        setImageUrl(image);
      }
    });
  }, [item]);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      const file = event.target.files[0];
      if (file) {
        uploadItemDisplayPicture(item, file).then((result) => {
          if (result) {
            toast.success("Bild geändert.");
            onUpdate();
          } else {
            toast.error("upload fehlgeschlagen!");
          }
        });
      }
    }
  };

  function setImage() {
    if (fileInput.current) {
      fileInput.current.click();
    }
  }

  function setPrice(newPrice: string) {
    changeShopItem(item, newPrice, "price").then((result) => {
      if (result) {
        toast.success("Preis geändert.");
        onUpdate();
      } else {
        toast.error("Änderung fehlgeschlagen!");
      }
    });
  }

  function setName(newName: string) {
    changeShopItem(item, newName, "displayname").then((result) => {
      if (result) {
        toast.success("Name geändert.");
        onUpdate();
      } else {
        toast.error("Änderung fehlgeschlagen!");
      }
    });
  }

  function setCategory(newCategory: string) {
    changeShopItem(item, newCategory, "category").then((result) => {
      if (result) {
        toast.success("Kategorie geändert.");
        onUpdate();
      } else {
        toast.error("Änderung fehlgeschlagen!");
      }
    });
  }

  function toggleEnable() {
    const newVal = !item.enabled;
    enableItem(item, newVal).then((result) => {
      if (result) {
        toast.success(item.id + (newVal ? " aktiviert!" : " deaktiviert!"));
        onUpdate();
      } else {
        toast.error("Änderung fehlgeschlagen!");
      }
    });
  }

  function deleteItem() {
    deleteShopItem(item).then((result) => {
      if (result) {
        toast.warning(item.id + " gelöscht!");
        onUpdate();
      } else {
        toast.error("Löschen fehlgeschlagen!");
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

        <ScrollDialog
          title="Gegenstand löschen?"
          onSubmit={deleteItem}
          trigger={
            <div className="Button danger-color">
              <Cross1Icon />
            </div>
          }
        >
          <h3 style={{ display: "flex" }}>
            {item.id}
            {item.enabled ? (
              <div className="good-color">
                <CheckCircledIcon />
              </div>
            ) : (
              <div className="danger-color">
                <CrossCircledIcon />
              </div>
            )}
          </h3>
          <div className="DisplayCard">
            <div style={{ display: "flex", alignItems: "center" }}>
              <Pencil2Icon /> {item.displayName}
            </div>
          </div>
          <div className="DisplayCard">
            <div style={{ display: "flex", alignItems: "center" }}>
              <BookmarkIcon />
              {item.category}
            </div>
          </div>
          <div className="DisplayCard">
            <div style={{ display: "flex", alignItems: "center" }}>
              <LightningBoltIcon />
              {formatMoney(item.price)}
            </div>
          </div>
        </ScrollDialog>
      </div>
      <Separator className="Separator" />
      <div className="SpreadContainer">
        <div className="SmallGridContainer" style={{ width: "60%" }}>
          <ButtonDialogChanger
            dialogName="Namen ändern"
            dialogDesc="Ändere hier den Namen des Items"
            onSubmit={setName}
            key={"NameChanger"}
            trigger={
              <div className="Button">
                <Pencil2Icon />
                {item.displayName}
              </div>
            }
          />
          <ButtonDialogChanger
            dialogName="Kategorie ändern"
            dialogDesc="Ändere hier die Kategorie des Items"
            onSubmit={setCategory}
            key={"CategoryChanger"}
            trigger={
              <div className="Button">
                <BookmarkIcon />
                {item.category}
              </div>
            }
          />
          <ButtonDialogChanger
            dialogName="Preis ändern"
            dialogDesc="Ändere hier den Preis des Items"
            onSubmit={setPrice}
            key={"PriceChanger"}
            trigger={
              <div className="Button">
                <LightningBoltIcon />
                {formatMoney(item.price)}
              </div>
            }
          />
        </div>
        <div style={{ width: "40%" }}>
          <AspectRatio ratio={1 / 1} onClick={setImage}>
            <div className="AspectRatio">
              <img
                className="Image"
                src={imageUrl}
                alt="Item Image"
                width={"100%"}
              />
            </div>
          </AspectRatio>
          <input
            type="file"
            ref={fileInput}
            onChange={handleFileChange}
            style={{ display: "none" }}
          />
        </div>
      </div>
    </div>
  );
}
