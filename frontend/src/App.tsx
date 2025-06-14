import "./App.css";
import { useWebsocket } from "./useWebsocket";
import { useStore } from "./store";

function App() {
  useWebsocket("production");

  const { players, teams, objectives } = useStore();

  return (
    <>
      <div className="container">
        <div>
          <h1>Players</h1>
          <pre>{JSON.stringify(players, null, 2)}</pre>
        </div>
        <div>
          <h1>Teams</h1>
          <pre>{JSON.stringify(teams, null, 2)}</pre>
        </div>
        <div>
          <h1>Objectives</h1>
          <pre>{JSON.stringify(objectives, null, 2)}</pre>
        </div>
      </div>
    </>
  );
}

export default App;
