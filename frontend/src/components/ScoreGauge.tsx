import { useShallow } from "zustand/shallow";
import { useStore } from "../store.ts";
import { TeamScore } from "./TeamScore.tsx";

function getAngle(leftScore: number, rightScore: number) {
  const maxDiff = 100;
  const diff = rightScore - leftScore;
  const clamped = Math.max(-maxDiff, Math.min(maxDiff, diff));
  return (clamped / maxDiff) * 90;
}

export function ScoreGauge() {
  const teams = useStore(useShallow((state) => state.teams));

  const getTeamScore = (teamName: string) => {
    const team = teams.find((team) => team.leader === teamName);
    return team?.score || 0;
  };

  const jakeScore = getTeamScore("JAKE");
  const badcopScore = getTeamScore("BADCOP");

  const needleAngle = getAngle(jakeScore, badcopScore);

  return (
    <div className="score-gauge">
      <TeamScore score={jakeScore} side="right" />
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 423 240" width="320" height="170" className="glass">
        <g>
          <path d="M211.5,8C324.031,8 407.207,83.639 422.832,196.17 C428.282,235.425 324.031,204.803 211.5,204.803 C98.969,204.803 -5.282,235.425 0.168,196.17 C15.793,83.639 98.969,8 211.5,8 Z" fill="#ffffff70" stroke="#000" strokeWidth="0" />
          <path d="M211.5,179.04C227.488,179.04 240.474,192.026 240.474,208.014 C240.474,224.001 227.488,236.988 211.5,236.988 C195.513,236.988 182.526,224.001 182.526,208.014 C182.526,192.026 195.513,179.04 211.5,179.04 Z" fill="#000" stroke="#000" strokeWidth="2" />
          <path className="gauge-needle" d="M205.11,205.491L208.5,31.392 C208.5,31.392 214.89,205.639 214.89,205.639 Z" fill="#000" stroke="#000" strokeWidth="2" style={{ transform: `rotate(${needleAngle}deg)` }} />
        </g>
      </svg>
      <TeamScore score={badcopScore} side="left" />
    </div>
  );
}
