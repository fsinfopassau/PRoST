import "../style.css";
import { RouterProvider, createBrowserRouter } from "react-router-dom";
import React, { useEffect, useState } from "react";
import { UserSelection } from "./Route-Components/UserSelection";

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
    const nextTheme = theme === "blue" ? "purple" : "blue";
    setTheme(nextTheme); // Update state using setTheme
  };

  // Ensure that loadTheme is called whenever the theme state changes
  useEffect(() => {
    loadTheme(theme);
    localStorage.setItem("theme", theme);
  }, [theme]);

  const router = createBrowserRouter([
    {
      path: "/",
      element: <UserSelection />,
    },
    {
      path: "*",
      element: (
        <h1>
          <img src="/icons/happy-manje/happy beer.svg" />
          Beer not found!
        </h1>
      ),
    },
  ]);

  return (
    <>
      <React.StrictMode>
        <button onClick={switchTheme}>Theme: {theme}</button>
        <RouterProvider router={router} />
      </React.StrictMode>
    </>
  );
}
