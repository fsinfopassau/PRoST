import {
  BarChartIcon,
  CookieIcon,
  FileTextIcon,
  GearIcon,
  HomeIcon,
  MagnifyingGlassIcon,
} from "@radix-ui/react-icons";
import { Tabs, TabsList, TabsTrigger } from "@radix-ui/react-tabs";
import { useLocation, useNavigate } from "react-router-dom";
import { LoginDialog } from "./LoginDialog";
import { getAuthorizedUser, isAdmin, isKiosk, isUser } from "../../SessionInfo";

export function Navbar(props: { switchTheme: () => void }) {
  const { switchTheme } = props;
  const navigate = useNavigate();
  const location = useLocation();

  function tabUpdate(newValue: string) {
    if (newValue === "root") {
      navigate("/");
    } else if (newValue === "buy") {
      const authUser = getAuthorizedUser();
      if (authUser && authUser.id) {
        navigate(`/shop/${authUser.id}`);
      }
    } else {
      navigate("/" + newValue);
    }
  }

  function getSelectedTabValue() {
    const newValue = location.pathname.split("/")[1];
    if (newValue.includes("root") || newValue.length === 0) {
      return "root";
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
                  value="root"
                  className="TabsTrigger"
                  onClick={() => tabUpdate("root")}
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
            {isUser() && !isKiosk() ? (
              <>
                <TabsTrigger
                  value="root"
                  className="TabsTrigger"
                  onClick={() => tabUpdate("root")}
                >
                  <HomeIcon />
                </TabsTrigger>
                <TabsTrigger
                  value="buy"
                  className="TabsTrigger"
                  onClick={() => {
                    tabUpdate("buy");
                  }}
                >
                  <CookieIcon />
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
