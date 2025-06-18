import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App.tsx";
import { HashRouter, Route, Routes } from "react-router";
import { Overlay } from "./Overlay.tsx";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <HashRouter>
      <Routes>
        <Route path="/" element={<App />} />
        <Route path="/overlay" element={<Overlay />} />
      </Routes>
    </HashRouter>
  </StrictMode>,
);
