import "./App.css";
import { useWebsocket } from "./useWebsocket";
import { DebugView } from "./DebugView";

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
        <DebugView />
      )}
    </>
  );
}

export default App;
