import { useEffect } from "react";
import { useStore } from "./store";

const rewriteHostname = () => {
  let portString = "";
  if (window.location.port !== "80") {
    portString = `:${window.location.port}`;
  }
  if (window.location.port === "5173") {
    portString = `:3009`;
  }
  return window.location.hostname + portString;
};

export const useWebsocket = (env: "production" | "auto") => {
  const { setKey } = useStore();
  useEffect(() => {
    let shouldConnect = true;
    let ws: WebSocket | null = null;

    let conjunctionJunction = () => {
      if (!shouldConnect) {
        return null;
      }

      let connStr = `${window.location.protocol.endsWith("s:") ? "wss" : "ws"}://${rewriteHostname()}/ws`;
      if (env === "production") {
        connStr = `wss://event.badcop.games/ws`;
      }
      const innerWs = new WebSocket(connStr);

      innerWs.onopen = () => {
        console.log("ws open");
      };

      innerWs.onmessage = (e: any) => {
        console.log("ws message");
        try {
          const data: any = JSON.parse(e.data);
          setKey(data.type, data.data);
          console.log("Got data", data);
        } catch (e) {
          console.error(e);
        }
      };

      innerWs.onclose = () => {
        console.log(`on close - ${!shouldConnect ? "not " : " "}reconnecting`);
        if (shouldConnect) {
          ws = conjunctionJunction();
        }
      };

      return innerWs;
    };

    ws = conjunctionJunction();

    return () => {
      shouldConnect = false;
      ws?.close();
    };
  }, []);
};
