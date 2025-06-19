import "./App.css";
import { useWebsocket } from "./useWebsocket";
import { PrimeReactProvider } from "primereact/api";
import "primeicons/primeicons.css";
import { Footer } from "./components/Footer.tsx";

function App() {
  useWebsocket("production");

  return (
    <>
      <PrimeReactProvider>
        <div className="container">
        </div>
        <Footer />
      </PrimeReactProvider>
    </>
  );
}

export default App;
