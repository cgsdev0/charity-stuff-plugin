import "../App.css";
import "primeicons/primeicons.css";

export function Overlay() {
  return (
    <div className="overlay">
      <iframe style={{ colorScheme: "none" }} width="1920" height="1080" src="https://overlays.tiltify.com/9VqqHWOPJVIui04iA-a6tB1Ba7DDZVIO" />
      <img src="overlay.png" />
    </div>
  );
}
