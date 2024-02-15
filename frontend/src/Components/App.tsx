import "../style.css";
import {
  BrowserRouter,
  Route,
  Routes,
  createBrowserRouter,
} from "react-router-dom";
import React, { useEffect, useState } from "react";
import { UserSelection } from "./Route-Components/UserSelection";
import { UserBox } from "./UserSelection/UserBox";
import { UserRole } from "../DTO/UserRole";
import { ErrorComponent } from "./Route-Components/ErrorComponent";
import { Label } from "@radix-ui/react-label";
import { TabChanger } from "./Util/TabChanger";
import { IconJarLogoIcon } from "@radix-ui/react-icons";

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

  const router = createBrowserRouter([
    {
      path: "/",
      errorElement: <ErrorComponent />,
      element: (
        <>
          <UserSelection switchTheme={switchTheme} />
        </>
      ),
    },
    {
      path: "stats",
      errorElement: <ErrorComponent />,
      element: (
        <>
          <Label>Stats incoming</Label>
        </>
      ),
    },
    {
      path: "users/:userid",
      errorElement: <ErrorComponent />,
      element: (
        <UserBox
          name="deimam"
          balance={3.3}
          role={UserRole.ADMINISTRATOR}
          enabled={true}
        />
      ),
    },
    {
      path: "*",
      element: <ErrorComponent />,
    },
  ]);

  return (
    <>
      <React.StrictMode>
        <BrowserRouter>
          <TabChanger />
          <Routes>
            <Route
              path="/"
              element={<UserSelection switchTheme={switchTheme} />}
            />
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
