import "../style.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import React, { useEffect, useState } from "react";
import { Navbar } from "./Util/Navbar";
import { ErrorComponent } from "./Util/ErrorTab";
import { ItemSelection } from "./SearchTab/ItemSelection";
import { ItemCheckout } from "./SearchTab/ItemCheckout";
import { Statistics } from "./StatisticsTab/Statistics";
import { History } from "./HistoryTab/History";
import { UserStatistics } from "./StatisticsTab/UserStatistics";
import { ItemSettings } from "./SettingsTab/ItemSettings";
import { Bounce, ToastContainer } from "react-toastify";
import { InvoiceTab } from "./InvoiceTab/InvoiceTab";
import { RootTab } from "./RootTab";
import { UserSettings } from "./SettingsTab/UserSettings";
import { PersonalInvoiceView } from "./PersonalView/PersonalInvoiceView";
import { PersonalHistoryView } from "./PersonalView/PersonalHistoryView";
import ScrollDialog from "./Util/ScrollDialog";
import { BlendingModeIcon } from "@radix-ui/react-icons";
import { Users } from "./Users";

const stylesAvailable = ["purple", "blue", "mc"];
export const BASE_PATH = import.meta.env.VITE_BASE_PATH || "";

function loadTheme(themeName: string) {
  // Remove existing theme stylesheet
  const oldThemeLink = document.getElementById("theme-stylesheet");
  if (oldThemeLink) {
    oldThemeLink.remove();
  }

  // Add new theme stylesheet
  const newThemeLink = document.createElement("link");
  newThemeLink.id = "theme-stylesheet";
  newThemeLink.rel = "stylesheet";
  newThemeLink.href = BASE_PATH + `/styles/${themeName}.css`;
  document.head.appendChild(newThemeLink);
}

export function App() {
  const [theme, setTheme] = useState(() => {
    const storedTheme = localStorage.getItem("theme");
    return storedTheme !== null ? storedTheme : stylesAvailable[0];
  });

  const switchTheme = () => {
    const currentIndex = stylesAvailable.indexOf(theme);
    const nextIndex = (currentIndex + 1) % stylesAvailable.length;
    const nextTheme = stylesAvailable[nextIndex];
    setTheme(nextTheme);
  };

  const HTMLDataInfos = () => {
    const [htmlContent, setHtmlContent] = useState("");

    useEffect(() => {
      fetch(BASE_PATH + `/data-info.html`)
        .then((response) => response.text())
        .then((data) => {
          setHtmlContent(data);
        })
        .catch((error) => console.error("Error loading HTML:", error));
    }, []);

    return <div dangerouslySetInnerHTML={{ __html: htmlContent }} />;
  };

  useEffect(() => {
    loadTheme(theme);
    localStorage.setItem("theme", theme);
  }, [theme]);

  return (
    <>
      <React.StrictMode>
        <div className="Main">
          <BrowserRouter basename={BASE_PATH}>
            <Navbar />
            <ToastContainer
              position="bottom-left"
              autoClose={4000}
              //limit={3}
              hideProgressBar={false}
              newestOnTop
              closeOnClick
              rtl={false}
              pauseOnFocusLoss={false}
              draggable
              pauseOnHover
              theme="light"
              transition={Bounce}
            />
            <div className="MainBody">
              <Routes>
                <Route path="/" element={<RootTab />} />
                <Route path="/me/invoices" element={<PersonalInvoiceView />} />
                <Route path="/me/history" element={<PersonalHistoryView />} />
                <Route path="/shop/:userId" element={<ItemSelection />} />
                <Route path="/shop/:userId/:itemId" element={<ItemCheckout />} />
                <Route path="/users" element={<Users />} />
                <Route path="/stats" element={<Statistics />} />
                <Route path="/stats/items" element={<Statistics />} />
                <Route path="/stats/users" element={<Statistics />} />
                <Route path="/stats/users/:userId" element={<UserStatistics />} />
                <Route path="/history" element={<History />} />
                <Route path="/invoices" element={<InvoiceTab />} />
                <Route path="/settings/items" element={<ItemSettings />} />
                <Route path="/settings/users" element={<UserSettings />} />
                <Route path="*" element={<ErrorComponent />} />
              </Routes>
            </div>
          </BrowserRouter>

          <ScrollDialog
            onSubmit={undefined}
            title="Datenschutzhinweise der PRoST-Sotware"
            trigger={<div id="site-data-info">Datenschutz</div>}
          >
            {HTMLDataInfos()}
          </ScrollDialog>
          <div id="theme-switch" onClick={switchTheme}>
            <BlendingModeIcon />
          </div>
        </div>
      </React.StrictMode>
    </>
  );
}
