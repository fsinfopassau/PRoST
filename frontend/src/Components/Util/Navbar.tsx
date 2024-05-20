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
import { getAuthorizedUser, isAdmin, isOnlyKiosk, isUser } from "../../SessionInfo";
import { BASE_PATH } from "../App";

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

    if (isOnlyKiosk() && newValue === "shop") {
      return "root";
    }

    if (newValue.includes("root") || newValue.length === 0) {
      return "root";
    } else if (location.pathname.startsWith("/me")) {
      return "me/" + location.pathname.split("/")[2];
    } else {
      return newValue;
    }
  }

  function kioskView() {
    return <>
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
    </>;
  }

  function userView() {
    return <>
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
        value="me/history"
        className="TabsTrigger"
        onClick={() => tabUpdate("me/history")}
      >
        <CalendarIcon />
      </TabsTrigger>
      <TabsTrigger
        value="me/invoices"
        className="TabsTrigger"
        onClick={() => tabUpdate("me/invoices")}
      >
        <FileTextIcon />
      </TabsTrigger></>;
  }

  function adminView() {
    return <>
      <TabsTrigger
        value="history"
        className="TabsTrigger"
        onClick={() => tabUpdate("history")}
      >
        <div className="orange">
          <CalendarIcon />
        </div>
      </TabsTrigger>
      <TabsTrigger
        value="invoices"
        className="TabsTrigger"
        onClick={() => tabUpdate("invoices")}
      >
        <div className="orange">
          <FileTextIcon />
        </div>
      </TabsTrigger>
      <TabsTrigger
        value="users"
        className="TabsTrigger"
        onClick={() => tabUpdate("users")}
      >
        <div className="orange">
          <PersonIcon />
        </div>
      </TabsTrigger>
      <TabsTrigger
        value="items"
        className="TabsTrigger"
        onClick={() => tabUpdate("items")}
      >
        <div className="orange">
          <DragHandleDots2Icon />
        </div>
      </TabsTrigger>
    </>;
  }

  return (
    <>
      <div id="tab-changer">
        <img
          onClick={switchTheme}
          src={`${BASE_PATH}/icons/happy-manje/happy beer.svg`}
        />
        <div style={{ display: "flex", gap: ".75rem", flexDirection: "column" }}>
          <Tabs value={getSelectedTabValue()} className="TabsRoot">
            <TabsList className="TabsList">
              {isOnlyKiosk() ? kioskView() :
                <>
                  {isUser() ? userView() : <></>}
                </>
              }
            </TabsList>
          </Tabs>
          {isAdmin() ?
            <Tabs value={getSelectedTabValue()} className="TabsRoot">
              <TabsList className="TabsList">
                {adminView()}
              </TabsList>
            </Tabs> : <></>
          }
        </div>
        <LoginDialog />
      </div >
    </>
  );
}
