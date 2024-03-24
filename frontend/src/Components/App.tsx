import "../style.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import React, { useEffect, useState } from "react";
import { Navbar } from "./Util/Navbar";
import { ErrorComponent } from "./Util/ErrorTab";
import { ItemSelection } from "./SearchTab/ItemSelection";
import { ItemCheckout } from "./SearchTab/ItemCheckout";
import { Statistics } from "./StatisticsTab/Statistics";
import { Settings } from "./SettingsTab/Settings";
import { UserStatistics } from "./StatisticsTab/UserStatistics";
import { AllUsersStatistics } from "./StatisticsTab/AllUsersStatistics";
import { ItemSettings } from "./SettingsTab/ItemSettings";
import { Bounce, ToastContainer } from "react-toastify";
import { InvoiceTab } from "./InvoiceTab/InvoiceTab";
import { RootTab } from "./RootTab";
import { UserSettings } from "./SettingsTab/UserSettings";

const stylesAvailable = ["purple", "blue"];

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
  newThemeLink.href = `/styles/${themeName}.css`;
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

  useEffect(() => {
    loadTheme(theme);
    localStorage.setItem("theme", theme);
  }, [theme]);

  return (
    <>
      <React.StrictMode>
        <div className="Main">
          <BrowserRouter>
            <Navbar switchTheme={switchTheme} />
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
                <Route
                  path="/"
                  element={<RootTab switchTheme={switchTheme} />}
                />
                <Route path="/invoice" element={<InvoiceTab />} />
                <Route path="/shop/:userId" element={<ItemSelection />} />
                <Route
                  path="/shop/:userId/:itemId"
                  element={<ItemCheckout />}
                />
                <Route path="/stats" element={<Statistics />} />
                <Route path="/stats/users" element={<AllUsersStatistics />} />
                <Route
                  path="/stats/users/:userId"
                  element={<UserStatistics />}
                />
                <Route path="/settings" element={<Settings />} />
                <Route path="/settings/items" element={<ItemSettings />} />
                <Route path="/settings/users" element={<UserSettings />} />
                <Route path="*" element={<ErrorComponent />} />
              </Routes>
            </div>
          </BrowserRouter>
        </div>
      </React.StrictMode>
    </>
  );
}
