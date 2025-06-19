import "./App.css";
import { useWebsocket } from "./useWebsocket";
import { PrimeReactProvider } from "primereact/api";
import { GoalsList } from "./components/GoalsList.tsx";
import "primeicons/primeicons.css";
import { Footer } from "./components/Footer.tsx";

function App() {
  useWebsocket("production");

  return (
    <>
      <PrimeReactProvider>
        <div className="container">
          <GoalsList teamName="BADCOP" />
          <GoalsList teamName="JAKE" />
        </div>
        <Footer />
      </PrimeReactProvider>
    </>
  );
}

export default App;
