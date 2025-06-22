import { Avatar } from "primereact/avatar";
import { useStore } from "../store.ts";
import { useShallow } from "zustand/shallow";
import { Tooltip } from "primereact/tooltip";

export function TeamHeader({ teamName, alignment }: { teamName: string; alignment: "flex-start" | "flex-end" }) {
  const team = useStore(useShallow((state) => state.teams.find((team) => team.leader === teamName))) || { players: [] };
  const playerNames = useStore(useShallow((state) => team.players.map((player) => state.players[player])));

  if (teamName === "JAKE") teamName = "JAKECREATES";

  return (
    <>
      <div className="team-header-container" style={{ alignItems: alignment }}>
        <h2 className="team-name">TEAM {teamName}</h2>
        <div className="player-icons" style={{ justifyContent: alignment }}>
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
