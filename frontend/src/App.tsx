import "./App.css";
import { useWebsocket } from "./useWebsocket";
import { PrimeReactProvider } from "primereact/api";
import "primeicons/primeicons.css";
import { TeamHeader } from "./components/TeamHeader.tsx";
import { GoalsList } from "./components/GoalsList.tsx";
import { ScoreGauge } from "./components/ScoreGauge.tsx";
import { TwitchIcons } from "./components/TwitchIcons.tsx";

function App() {
  useWebsocket("production");

  return (
    <>
      <PrimeReactProvider>
        <div className="background">
          <div className="jake"></div>
          <div className="badcop"></div>
        </div>
        <div className="main">
          <header>
            <TeamHeader teamName="JAKE" alignment="flex-start" />
            <ScoreGauge />
            <TeamHeader teamName="BADCOP" alignment="flex-end" />
          </header>
          <div className="goals">
            <section>
              <GoalsList teamName="JAKE" />
              <GoalsList teamName="BADCOP" />
            </section>
          </div>
          <footer>
            Live Now
            <TwitchIcons />
          </footer>
        </div>
      </PrimeReactProvider>
    </>
  );
}

export default App;
