import { Avatar } from "primereact/avatar";
import { useStore } from "../store.ts";
import { useShallow } from "zustand/shallow";
import { Tooltip } from "primereact/tooltip";

export function TeamHeader({ teamName, alignment }: { teamName: string; alignment: "flex-start" | "flex-end" }) {
  const team = useStore(useShallow((state) => state.teams.find((team) => team.leader === teamName)));
  const players = useStore(useShallow((state) => state.players));
  const playerNames: string[] = [];
  Object.keys(players).forEach((id) => {
    if (team?.players.includes(id)) {
      playerNames.push(players[id]);
    }
  });

  return (
    <>
      <div style={{ display: "flex", flexDirection: "column", justifyContent: "space-between", alignItems: alignment, maxWidth: "400px", padding: "20px" }}>
        <h2 style={{ color: "white", marginTop: "0px" }}>{teamName}</h2>
        <div style={{ height: 32, display: "flex", flexWrap: "wrap", gap: "5px", alignItems: "center", justifyContent: alignment }}>
          {playerNames.map((player) => (
            <>
              <Avatar key={player} image={`https://mc-heads.net/avatar/${player}`} size="normal" data-pr-tooltip={player} />
              <Tooltip target={`[data-pr-tooltip=${player}]`} position={alignment == "flex-start" ? "right" : "left"} />
            </>
          ))}
        </div>
      </div>
    </>
  );
}
