import { BookmarkIcon, CameraIcon, Cross1Icon, Pencil2Icon } from "@radix-ui/react-icons";
import { ShopItem } from "../../Types/ShopItem";
import { formatMoney } from "../../Types/User";
import { Separator } from "@radix-ui/react-separator";
import { Switch, SwitchThumb } from "@radix-ui/react-switch";
import { enableItem } from "../Util/Queries";

export function ItemSetting(props: {item: ShopItem}){

    const {item} = props;


    function setImage() {
        console.log("TODO Image");
    }
  
    function setPrice(){
        console.log("TODO Price");
    }
  
    function setName(){
        console.log("TODO Name");
    }
  
    function deleteItem(){
        console.log("TODO Delete");
    }

    function toggleEnable(){
        const newVal = !item.enabled;

        enableItem(item,newVal).then((result) => {
            if(result){
                item.enabled = newVal;
            }
        });
    }

    return <div className="DisplayCard">
    <div className="SpreadContainer">
      <div className="bold">
        {item.id}
      </div>

      <Switch className="SwitchRoot" defaultChecked={item.enabled} onCheckedChange={toggleEnable}>
        <SwitchThumb className="SwitchThumb" />
      </Switch>

      <div className="Button" onClick={deleteItem} style={{color: "red"}}>
        <Cross1Icon/>
      </div>
    </div>
    <Separator className="Separator" />
    <div className="SpreadContainer">
      <div className="Button" onClick={setName}>
        <Pencil2Icon/>{item.displayName} </div>
      <div className="Button" onClick={setPrice}>
        <BookmarkIcon/>{item.category}
      </div>
      <div className="Button" onClick={setImage}>
        <CameraIcon/>
      </div>
      <div className="Button thin" onClick={setPrice}>
      {formatMoney(item.price)}
      </div>
    </div>
  </div>

}