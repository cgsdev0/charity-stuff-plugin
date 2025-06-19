import { useStore } from "../store.ts";
import { useShallow } from "zustand/shallow";
import { GoalCard, type Goal } from "./GoalCard.tsx"

export function GoalsList({ teamName }: { teamName: string }) {
  const { players, objectives } = useStore();
  const team = useStore(useShallow((state) => state.teams.find((team) => team.leader === teamName)));
  const finishedGoals = team?.objectives || [];

  const Goals = Object.values(objectives).map((objective) => {
    const finishedGoal = finishedGoals.filter((goal) => goal.key.key === objective.key);
    let unlockedBy: string[] = [];
    let unlocked: boolean = false;
    if (finishedGoal.length > 0) {
      unlocked = true;
      unlockedBy = finishedGoal.map((goal) => {
        return players[goal.value.unlockedBy]
      })
    }
    const goal: Goal = {
      name: objective.name,
      points: objective.worth,
      kind: objective.kind,
      unlocked: unlocked,
      description: objective.description,
      by: unlockedBy,
      id: objective.key,
    };
    return goal;
  });
  // Sort : unlocked first, then by points
  Goals.sort((a, b) => {
    if (a.unlocked === b.unlocked) {
      return a.points - b.points;
    }
    return a.unlocked ? 1 : -1;
  });

  return (
    <div className="grid-container" style={{ overflowY: "auto", height: "80%", scrollbarColor: "#ffffff #00000000" }}>
      {Goals.map((goal) => (
        <GoalCard goal={goal} key={goal.id}/>
      ))}
    </div>
  );
}
