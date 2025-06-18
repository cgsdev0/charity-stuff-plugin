import "./App.css";
import { useWebsocket } from "./useWebsocket";
import { DebugView } from "./DebugView";
import { PrimeReactProvider } from "primereact/api";
import { GoalsList } from "./GoalsList";
import 'primeicons/primeicons.css';

function App() {
  useWebsocket("production");

  const hash = window.location.hash || "";
  console.log(hash);
  return (
    <>
      {hash === "#overlay" ? (
        <div className="overlay">
          <iframe width="1920" height="1080" src="https://overlays.tiltify.com/9VqqHWOPJVIui04iA-a6tB1Ba7DDZVIO" />
          <img src="overlay.png" />
        </div>
      ) : (
        <PrimeReactProvider>
          {/* <DebugView /> */}
          <div className="container">
            <GoalsList teamName="BADCOP" />
            <GoalsList teamName="JAKE" />
          </div>
        </PrimeReactProvider>
      )}
    </>
  );
}

export default App;
