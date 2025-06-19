import "./App.css";
import { useWebsocket } from "./useWebsocket";
import { PrimeReactProvider } from "primereact/api";
import "primeicons/primeicons.css";
import { Footer } from "./components/Footer.tsx";
import { TeamHeader } from "./TeamHeader.tsx";
import { GoalsList } from "./components/GoalsList.tsx";

function App() {
  useWebsocket("production");

  return (
    <>
      <PrimeReactProvider>
        <div style={{ display: "flex", flexDirection: "row", position: "absolute"}}>
          <div className="half" style={{ background: "linear-gradient(to top, #ed0852, #fe9902",  display: "flex", flexDirection: "column", alignItems: "flex-start"}}>
            <TeamHeader teamName="JAKE" alignment="flex-start" />
            <GoalsList teamName="JAKE" />
          </div>
          <div className="half" style={{ background: "linear-gradient(to bottom, #02a8f8, #5b24e3)", display: "flex", flexDirection: "column", alignItems: "flex-end" }}>
            <TeamHeader teamName="BADCOP" alignment="flex-end" />
            <GoalsList teamName="BADCOP" />
            <Footer /> 
          </div>
        </div>
      </PrimeReactProvider>
    </>
  );
}

export default App;
