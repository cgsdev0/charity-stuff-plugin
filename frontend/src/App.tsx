import "./App.css";
import { useWebsocket } from "./useWebsocket";
import { PrimeReactProvider } from "primereact/api";
import { GoalsList } from "./GoalsList";
import "primeicons/primeicons.css";

function App() {
  useWebsocket("production");

  return (
    <>
      <PrimeReactProvider>
        <div className="container">
          <GoalsList teamName="BADCOP" />
          <GoalsList teamName="JAKE" />
        </div>
      </PrimeReactProvider>
    </>
  );
}

export default App;
