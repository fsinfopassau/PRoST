import {
  BarChartIcon,
  FileTextIcon,
  GearIcon,
  MagnifyingGlassIcon,
} from "@radix-ui/react-icons";
import { Tabs, TabsList, TabsTrigger } from "@radix-ui/react-tabs";
import { useLocation, useNavigate } from "react-router-dom";
import { LoginDialog } from "./LoginDialog";
import { isAdmin, isKiosk } from "../../SessionInfo";

export function Navbar(props: { switchTheme: () => void }) {
  const { switchTheme } = props;
  const navigate = useNavigate();
  const location = useLocation();

  function tabUpdate(newValue: string) {
    if (newValue === "shop") {
      navigate("/");
    } else {
      navigate("/" + newValue);
    }
  }

  function getSelectedTabValue() {
    const newValue = location.pathname.split("/")[1];
    if (newValue.includes("search") || newValue.length === 0) {
      return "shop";
    } else {
      return newValue;
    }
  }

  return (
    <>
      <div id="tab-changer">
        <img onClick={switchTheme} src="/icons/happy-manje/happy beer.svg" />
        <Tabs defaultValue={getSelectedTabValue()} className="TabsRoot">
          <TabsList className="TabsList">
            {isKiosk() ? (
              <>
                <TabsTrigger
                  value="shop"
                  className="TabsTrigger"
                  onClick={() => tabUpdate("shop")}
                >
                  <MagnifyingGlassIcon />
                </TabsTrigger>
                <TabsTrigger
                  value="stats"
                  className="TabsTrigger"
                  onClick={() => tabUpdate("stats")}
                >
                  <BarChartIcon />
                </TabsTrigger>
              </>
            ) : (
              <></>
            )}
            {isAdmin() ? (
              <>
                <TabsTrigger
                  value="invoice"
                  className="TabsTrigger"
                  onClick={() => tabUpdate("invoice")}
                >
                  <FileTextIcon />
                </TabsTrigger>
                <TabsTrigger
                  value="settings"
                  className="TabsTrigger"
                  onClick={() => tabUpdate("settings")}
                >
                  <GearIcon />
                </TabsTrigger>
              </>
            ) : (
              <></>
            )}
          </TabsList>
        </Tabs>
        <LoginDialog />
      </div>
    </>
  );
}
