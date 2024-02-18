import "../style.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import React, { useEffect, useState } from "react";
import { TabChanger } from "./Util/Navbar";
import { ErrorComponent } from "./Util/ErrorTab";
import { UserSelection } from "./SearchTab/UserSelection";
import { ItemSelection } from "./SearchTab/ItemSelection";
import { ItemCheckout } from "./SearchTab/ItemCheckout";
import { Statistics } from "./StatisticsTab/Statistics";
import { Settings } from "./SettingsTab/Settings";
import { UserStatistics } from "./StatisticsTab/UserStatistics";
import { AllUsersStatistics } from "./StatisticsTab/AllUsersStatistics";

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
        <BrowserRouter>
          <TabChanger switchTheme={switchTheme} />
          <Routes>
            <Route
              path="/"
              element={<UserSelection switchTheme={switchTheme} />}
            />
            <Route path="/shop/:userId" element={<ItemSelection />} />
            <Route path="/shop/:userId/:itemId" element={<ItemCheckout />} />
            <Route path="/stats" element={<Statistics />} />
            <Route path="/stats/users" element={<AllUsersStatistics />} />
            <Route path="/stats/users/:userId" element={<UserStatistics />} />
            <Route path="/settings" element={<Settings />} />
            <Route
              path="*"
              element={<ErrorComponent switchTheme={switchTheme} />}
            />
          </Routes>
        </BrowserRouter>
      </React.StrictMode>
    </>
  );
}
