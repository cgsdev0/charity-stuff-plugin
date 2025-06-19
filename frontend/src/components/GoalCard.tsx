import { Avatar } from "primereact/avatar";
import { Tooltip } from "primereact/tooltip";

export interface Goal {
    name: string;
    description?: string;
    points: number;
    kind: string;
    unlocked: boolean;
    id: string;
    by: string[];
}
    

export function GoalCard({ goal }: { goal: Goal }) {
    return (
        <div style={{ backgroundColor: "#ffffff70", borderRadius: "15px", padding: "10px", display: "flex", flexDirection: "column", color: "black" }}>
            <div style={{ display: "flex", flexDirection: "row", justifyContent: "space-between", alignItems: "baseline", position: "relative" }}>
                <span style={{fontSize: "20px"}}>{goal.name}</span>
                <span>{goal.points}
                    {goal.by.length > 0 && <span style={{color: "white", backgroundColor: "red", border: "solid 2px white", padding: "1px 7px", margin: "0 2px", borderRadius: "8px", fontSize: "12px", transform: "rotate(15deg)", position: "absolute", right: "-20px", top: "-20px"}}>X{goal.by.length.toString()}</span>}
                </span>
            </div>
            <div style={{ display: "flex", flexDirection: "row", justifyContent: "space-between"}}>
                <span>{goal.description}</span>
                <div style={{ display: "flex", flexWrap: "wrap", gap: '2px', alignItems: "center", justifyContent: "flex-end" }}>
                    {goal.by.slice(0, 3).map((player) => (
                        <Avatar
                            key={player}
                            image={`https://mc-heads.net/avatar/${player}`}
                            size="normal" 
                            data-pr-tooltip={player}
                        />
                    ))}
                </div>
            </div>
        </div>
    )
}