import { Avatar } from "primereact/avatar";
import { useStore } from "../store.ts";
import { useShallow } from "zustand/shallow";
import { Tooltip } from "primereact/tooltip";

export function TeamHeader({ teamName, alignment }: { teamName: string; alignment: "flex-start" | "flex-end" }) {
  const team = useStore(useShallow((state) => state.teams.find((team) => team.leader === teamName))) || { players: [] };
  const players = useStore((state) => state.players);

  if (teamName === "JAKE") teamName = "JAKECREATES";

  return (
    <>
      <div className="team-header-container" style={{ alignItems: alignment }}>
        <h2 className="team-name">TEAM {teamName}</h2>
        <div className="player-icons" style={{ justifyContent: alignment }}>
          {team.players.map((uuid) => {
            const name = players[uuid];
            return (
              <span key={uuid}>
                <Avatar image={`https://mc-heads.net/avatar/${uuid}`} size="normal" data-pr-tooltip={name} />
                <Tooltip target={`[data-pr-tooltip=${name}]`} position={alignment == "flex-start" ? "right" : "left"} />
              </span>
            );
          })}
        </div>
      </div>
    </>
  );
}
