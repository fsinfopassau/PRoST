import "../style.css";
import { RouterProvider, createBrowserRouter } from "react-router-dom";
import React, { useEffect, useState } from "react";
import { UserSelection } from "./Route-Components/UserSelection";

const stylesAvailable = ["purple", "blue"];

export function App() {
  const [theme, setTheme] = useState(stylesAvailable[0]);

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

  const switchTheme = () => {
    const nextTheme = theme === "blue" ? "purple" : "blue";

    setTheme(nextTheme); // Update state using setTheme
    localStorage.setItem("theme", nextTheme);
    console.log("switched " + nextTheme);
    loadTheme(nextTheme);
  };

  useEffect(() => {
    // Load the theme from local storage if available
    const storedTheme = localStorage.getItem("theme");
    if (storedTheme) {
      setTheme(storedTheme); // Set the initial state from local storage
    }
  }, []);

  // Ensure that loadTheme is called whenever the theme state changes
  useEffect(() => {
    loadTheme(theme);
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
