import "./App.css";
import { useWebsocket } from "./useWebsocket";
import { PrimeReactProvider } from "primereact/api";
import "primeicons/primeicons.css";
import { Footer } from "./components/Footer.tsx";
import { TeamHeader } from "./components/TeamHeader.tsx";
import { GoalsList } from "./components/GoalsList.tsx";
import {ScoreGauge} from "./components/ScoreGauge.tsx";

function App() {
  useWebsocket("production");

  return (
    <>
      <PrimeReactProvider>
        <div className="main">
          <div className="half jake-half">
            <TeamHeader teamName="JAKE" alignment="flex-start" />
            <GoalsList teamName="JAKE" />
          </div>
          <div className="half badcop-half">
            <TeamHeader teamName="BADCOP" alignment="flex-end" />
            <GoalsList teamName="BADCOP" />
          </div>
        </div>
        <Footer/>
        <ScoreGauge/>
      </PrimeReactProvider>
    </>
  );
}

export default App;
