import { useStore } from "../store.ts";
import { useShallow } from "zustand/shallow";
import { GoalCard, type Goal } from "./GoalCard.tsx";

export function GoalsList({ teamName }: { teamName: string }) {
  const { players, objectives } = useStore();
  const team = useStore(useShallow((state) => state.teams.find((team) => team.leader === teamName)));
  const finishedGoals = team?.objectives || [];

  const Goals = Object.values(objectives).map((objective) => {
    const finishedGoal = finishedGoals.filter((goal) => goal.key.key === objective.key);
    let unlockedBy: string[] = [];
    let unlocked: boolean = false;
    if (finishedGoal.length > 0) {
      if (objective.kind === "PER_TEAM") {
        unlocked = true;
      }
      unlockedBy = finishedGoal.map((goal) => {
        return players[goal.value.unlockedBy];
      });
    }
    const goal: Goal = {
      name: objective.name,
      points: objective.worth,
      kind: objective.kind,
      unlocked: unlocked,
      description: objective.description,
      advancement: objective.advancement,
      by: unlockedBy,
      id: objective.key,
    };
    return goal;
  });

  Goals.sort((a, b) => {
    // if (a.unlocked !== b.unlocked) {
    //   return a.unlocked ? 1 : -1;
    // }
    if (a.points !== b.points) {
      return a.points - b.points;
    }
    return a.id.localeCompare(b.id);
  });

  return (
    <div className="grid-container">
      {Goals.map((goal) => (
        <GoalCard goal={goal} key={goal.id} />
      ))}
    </div>
  );
}
