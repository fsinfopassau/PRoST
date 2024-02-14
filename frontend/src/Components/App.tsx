import "../styles/styles.css";
import "../styles/purple.css";
import { RouterProvider, createBrowserRouter } from "react-router-dom";
import React from "react";
import { UserSelection } from "./Route-Components/UserSelection";

export function App() {
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
        <RouterProvider router={router} />
      </React.StrictMode>
    </>
  );
}
