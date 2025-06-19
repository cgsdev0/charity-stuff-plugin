import { Avatar } from "primereact/avatar";
import { useStore } from "./store";
import { useShallow } from "zustand/shallow";
import { Tooltip } from "primereact/tooltip";


export function TeamHeader({ teamName, alignment }: { teamName: string, alignment: "flex-start" | "flex-end" }) {
    const team = useStore(useShallow((state) => state.teams.find((team) => team.leader === teamName)));
    const players = useStore(useShallow((state) => state.players));
    const playerNames : string[] = []
    Object.keys(players).forEach((id) => {
        if (team?.players.includes(id)) {
            playerNames.push(players[id]);
        }
    });

    return (
        <>
            <div style={{ display: "flex", flexDirection: "column", justifyContent: "space-between", alignItems: alignment, maxWidth: "400px" }}>
                <h2 style={{color: "white"}}>{teamName}</h2>
                <div style={{ display: "flex", flexWrap: "wrap", gap: '5px', alignItems: "center", justifyContent: alignment }}> 
                    {playerNames.map((player) => (
                        <>
                            <Avatar
                                key={player}
                                image={`https://mc-heads.net/avatar/${player}`}
                                size="normal" 
                                data-pr-tooltip={player}
                            />
                            <Tooltip target={`[data-pr-tooltip=${player}]`} position="top" />
                        </>
                    ))}
                </div>
            </div>
        </>
    )
}