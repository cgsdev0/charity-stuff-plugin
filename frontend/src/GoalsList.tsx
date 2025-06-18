import { useStore } from "./store";
import { DataTable, type DataTableExpandedRows, type DataTableValueArray } from "primereact/datatable";
import { Column } from "primereact/column";
import { Avatar } from "primereact/avatar";
import { AvatarGroup } from "primereact/avatargroup";
import { useState } from "react";
import { Tooltip } from "primereact/tooltip";
import { useShallow } from "zustand/shallow";

export function GoalsList({ teamName }: { teamName: string }) {
  const { players, teams, objectives } = useStore();
  const team = useStore(useShallow((state) => state.teams.find((team) => team.leader === teamName)));
  const finishedGoals = team?.objectives || [];
  const [expandedRows, setExpandedRows] = useState<DataTableExpandedRows | DataTableValueArray | undefined>();

  interface Goal {
    name: string;
    description?: string;
    points: number;
    kind: string;
    unlocked: boolean;
    by: string[];
  }

  const Goals: Goal[] = [];

  Object.values(objectives).forEach((objective) => {
    const finishedGoal = finishedGoals.find((goal) => goal.key.key === objective.key);
    let unlockedBy: string[] = [];
    let unlocked: boolean = false;
    if (finishedGoal) {
      unlocked = true;
      unlockedBy = [players[finishedGoal.value.unlockedBy || ""] || ""];
    }
    const goal: Goal = {
      name: objective.name,
      points: objective.worth,
      kind: objective.kind,
      unlocked: unlocked,
      by: unlockedBy,
    };
    Goals.push(goal);
  });
  // Sort : unlocked first, then by points
  Goals.sort((a, b) => {
    if (a.unlocked === b.unlocked) {
      return a.points - b.points;
    }
    return a.unlocked ? 1 : -1;
  });

  const rowExpansionTemplate = (rowData: Goal) => {
    return <span>{rowData.description}</span>;
  };

  const footer = `Total Points: ${teams.find((team) => team.leader === teamName)?.score || 0}`;

  return (
    <div className="vcontainer">
      <h2 className="text-center">{teamName} Goals</h2>
      <DataTable value={Goals} footer={footer} expandedRows={expandedRows} onRowToggle={(e) => setExpandedRows(e.data)} rowExpansionTemplate={rowExpansionTemplate}>
        <Column expander className="toggle-arrow" />
        <Column
          field="name"
          header="Goal"
          className="goal-column"
          body={(rowData) => (
            <div>
              {rowData.kind === "PER_TEAM" && <span className="pi pi-users" data-pr-tooltip="Team"></span>}
              {rowData.kind === "PER_PLAYER" && <span className="pi pi-user" data-pr-tooltip="Individual"></span>}
              <span>{rowData.name}</span>
            </div>
          )}
        />
        <Column field="points" header="Points" className="points-column" />
        <Column
          field="unlocked"
          header="By"
          className="unlocked-column"
          body={(rowData) => (
            <div>
              <AvatarGroup>
                {rowData.by.map((player: string, index: number) => {
                  if (index <= 4) {
                    return (
                      <>
                        <Avatar key={index} image={`https://mc-heads.net/avatar/${player}`} size="normal" data-pr-tooltip={player} />
                        <Tooltip target={`[data-pr-tooltip=${player}]`} position="top" />
                      </>
                    );
                  }
                  if (index === 5) {
                    return <Avatar key={index} label={`+${rowData.by.length - 5}`} size="normal" />;
                  }
                  return null;
                })}
              </AvatarGroup>
              {rowData.by.length === 0 ? "N/A" : ""}
            </div>
          )}
        />
      </DataTable>
      <Tooltip target=".pi" position="top" />
    </div>
  );
}
