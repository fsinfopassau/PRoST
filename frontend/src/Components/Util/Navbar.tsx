import {
  BarChartIcon,
  CalendarIcon,
  CookieIcon,
  FileTextIcon,
  HomeIcon,
  MagnifyingGlassIcon,
  PersonIcon,
  TokensIcon,
} from "@radix-ui/react-icons";
import { Tabs, TabsList, TabsTrigger } from "@radix-ui/react-tabs";
import { useLocation, useNavigate } from "react-router-dom";
import { LoginDialog } from "./LoginDialog";
import { getAuthorizedUser, isAdmin, isOnlyKiosk, isOnlyUser, isUser, resetSession } from "../../SessionInfo";
import { BASE_PATH } from "../App";
import { useEffect } from "react";

export function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const handleKeyPress = (event: KeyboardEvent) => {
      if (event.key === "F8" || event.key === "f8") {
        resetSession();
        navigate("/");
        window.location.reload();
      }
    };

    document.addEventListener("keydown", handleKeyPress);

    return () => {
      document.removeEventListener("keydown", handleKeyPress);
    };
  });

  function navigateRoot() {
    navigate("");
  }

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
    return (
      <>
        <TabsTrigger value="root" className="TabsTrigger" onClick={() => tabUpdate("root")}>
          <MagnifyingGlassIcon />
        </TabsTrigger>
        <TabsTrigger value="stats" className="TabsTrigger" onClick={() => tabUpdate("stats")}>
          <BarChartIcon />
        </TabsTrigger>
      </>
    );
  }

  function userView() {
    return (
      <>
        <TabsTrigger value="root" className="TabsTrigger" onClick={() => tabUpdate("root")}>
          <HomeIcon />
        </TabsTrigger>
        <TabsTrigger value="shop" className="TabsTrigger" onClick={() => tabUpdate("shop-self")}>
          <CookieIcon />
        </TabsTrigger>
        {isOnlyUser() ? (
          <>
            <TabsTrigger value="me/history" className="TabsTrigger" onClick={() => tabUpdate("me/history")}>
              <CalendarIcon />
            </TabsTrigger>
            <TabsTrigger value="me/invoices" className="TabsTrigger" onClick={() => tabUpdate("me/invoices")}>
              <FileTextIcon />
            </TabsTrigger>
          </>
        ) : (
          <></>
        )}
      </>
    );
  }

  function adminView() {
    return (
      <>
        <TabsTrigger value="stats" className="TabsTrigger" onClick={() => tabUpdate("stats")}>
          <div className="important-color">
            <BarChartIcon />
          </div>
        </TabsTrigger>
        <TabsTrigger value="history" className="TabsTrigger" onClick={() => tabUpdate("history")}>
          <div className="important-color">
            <CalendarIcon />
          </div>
        </TabsTrigger>
        <TabsTrigger value="invoices" className="TabsTrigger" onClick={() => tabUpdate("invoices")}>
          <div className="important-color">
            <FileTextIcon />
          </div>
        </TabsTrigger>
        <TabsTrigger value="users" className="TabsTrigger" onClick={() => tabUpdate("users")}>
          <div className="important-color">
            <PersonIcon />
          </div>
        </TabsTrigger>
        <TabsTrigger value="items" className="TabsTrigger" onClick={() => tabUpdate("items")}>
          <div className="important-color">
            <TokensIcon />
          </div>
        </TabsTrigger>
      </>
    );
  }

  return (
    <div id="tab-changer">
      <img onClick={navigateRoot} src={`${BASE_PATH}/icons/happy-manje/happy beer.svg`} alt={"Logo"} />
      <Tabs value={getSelectedTabValue()}>
        <TabsList className="TabsList">
          {isOnlyKiosk() ? kioskView() : isUser() ? userView() : null}
          {isAdmin() ? adminView() : <></>}
        </TabsList>
      </Tabs>
      {!isOnlyKiosk() ? <LoginDialog /> : <></>}
    </div>
  );
}
