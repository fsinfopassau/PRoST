import {
  BarChartIcon,
  CalendarIcon,
  CookieIcon,
  DragHandleDots2Icon,
  FileTextIcon,
  HomeIcon,
  MagnifyingGlassIcon,
  PersonIcon,
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
    } else if (newValue === "shop-self") {
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
    } else if (location.pathname.startsWith("/me")) {
      return location.pathname.split("/")[2];
    } else {
      return newValue;
    }
  }

  return (
    <>
      <div id="tab-changer">
        <img onClick={switchTheme} src="/icons/happy-manje/happy beer.svg" />
        <Tabs value={getSelectedTabValue()} className="TabsRoot">
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
                  value="history"
                  className="TabsTrigger"
                  onClick={() => tabUpdate("history")}
                >
                  <CalendarIcon />
                </TabsTrigger>
                <TabsTrigger
                  value="invoices"
                  className="TabsTrigger"
                  onClick={() => tabUpdate("invoices")}
                >
                  <FileTextIcon />
                </TabsTrigger>
                <TabsTrigger
                  value="users"
                  className="TabsTrigger"
                  onClick={() => tabUpdate("users")}
                >
                  <PersonIcon />
                </TabsTrigger>
                <TabsTrigger
                  value="items"
                  className="TabsTrigger"
                  onClick={() => tabUpdate("items")}
                >
                  <DragHandleDots2Icon />
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
                  value="shop"
                  className="TabsTrigger"
                  onClick={() => {
                    tabUpdate("shop-self");
                  }}
                >
                  <CookieIcon />
                </TabsTrigger>
                <TabsTrigger
                  value="history"
                  className="TabsTrigger"
                  onClick={() => tabUpdate("me/history")}
                >
                  <CalendarIcon />
                </TabsTrigger>
                <TabsTrigger
                  value="invoices"
                  className="TabsTrigger"
                  onClick={() => tabUpdate("me/invoices")}
                >
                  <FileTextIcon />
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
