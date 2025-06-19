import {useStore} from "../store.ts";
import {useShallow} from "zustand/shallow";
import NumberFlow from "@number-flow/react";

export function TeamScore({teamName}: { teamName: string}) {
  const team = useStore(useShallow((state) => state.teams.find((team) => team.leader === teamName)));
  const score = team?.score || 0;

  return (
    <NumberFlow value={score} className= "team-score"/>
  )
}
