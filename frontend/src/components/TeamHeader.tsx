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

  if (teamName === "JAKE") teamName = "JAKECREATES";
  return (
    <>
      <div style={{ display: "flex", flex: 1, flexDirection: "column", justifyContent: "space-between", alignItems: alignment, padding: "20px" }}>
        <h2 style={{ color: "white", marginTop: "0px" }}>TEAM {teamName}</h2>
        <div style={{ height: 32, display: "flex", flexWrap: "wrap", gap: "5px", alignItems: "center", justifyContent: alignment }}>
          {playerNames.map((player) => (
            <span key={player}>
              <Avatar image={`https://mc-heads.net/avatar/${player}`} size="normal" data-pr-tooltip={player} />
              <Tooltip target={`[data-pr-tooltip=${player}]`} position={alignment == "flex-start" ? "right" : "left"} />
            </span>
          ))}
        </div>
      </div>
    </>
  );
}
