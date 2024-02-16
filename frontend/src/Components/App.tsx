import "../style.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import React, { useEffect, useState } from "react";
import { TabChanger } from "./Util/Navbar";
import { ErrorComponent } from "./Route-Components/ErrorTab";
import { UserSelection } from "./SearchTab/UserSelection";
import { ItemSelection } from "./SearchTab/ItemSelection";
import { ItemCheckout } from "./SearchTab/ItemCheckout";

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
            <Route path="/shop/:userid" element={<ItemSelection />} />
            <Route path="/shop/:userid/:itemid" element={<ItemCheckout />} />
            <Route path="/stats" element={<>Rendere Statistiken hier!</>} />
            <Route path="/settings" element={<>Rendere Settings hier!</>} />
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
