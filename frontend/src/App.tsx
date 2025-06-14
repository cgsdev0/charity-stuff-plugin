import "./App.css";
import { useWebsocket } from "./useWebsocket";
import { DebugView } from "./DebugView";

function App() {
  useWebsocket("auto");

  return (
    <>
      <DebugView />
    </>
  );
}

export default App;
