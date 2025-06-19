import { Avatar } from "primereact/avatar";
import { AvatarGroup } from "primereact/avatargroup";

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
    <div className={`goal-card ${goal.unlocked ? "unlocked" : ""}`}>
      <div className="col">
        <span className="name">{goal.name}</span>
        <span className="description">{goal.description}</span>
      </div>
      <div className="col right">
        <div className="heads">
          {goal.by.slice(0, 4).map((player) => (
            <Avatar key={player} image={`https://mc-heads.net/avatar/${player}`} size="normal" data-pr-tooltip={player} style={{ borderRadius: "0" }} />
          ))}
        </div>
        <span className="points">
          {goal.points}
          {goal.by.length > 0 && goal.kind === "PER_PLAYER" && <span className="combo">x{goal.by.length}</span>}
          {/*goal.by.length > 0 && goal.kind === "PER_PLAYER" && <Sticker amount={goal.by.length} />*/}
        </span>
      </div>
    </div>
  );
}

function Sticker({ amount }: { amount: number }) {
  return (
    <span
      style={{
        color: "white",
        backgroundColor: "red",
        border: "solid 2px white",
        padding: "0px 6px",
        borderRadius: "8px",
        fontSize: "12px",
        transform: "rotate(12deg)",
        position: "absolute",
        right: "-16px",
        lineHeight: "initial",
        top: "-2px",
      }}
    >
      X{amount.toString()}
    </span>
  );
}
