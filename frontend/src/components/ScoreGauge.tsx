import {useShallow} from "zustand/shallow";
import {useStore} from "../store.ts";
import {TeamScore} from "./TeamScore.tsx";

function getAngle(leftScore: number, rightScore: number) {
  const diff = rightScore - leftScore;
  let angle = 0;

  if (diff === 0) return 0;
  const avgScore = (leftScore + rightScore) / 2;
  const dampening = 1 + (avgScore / 50);
  const dampedDiff = diff / dampening;
  const maxScore = Math.max(leftScore, rightScore);
  const normalizedDampened = maxScore > 0 ? dampedDiff / maxScore : 0;
  angle = normalizedDampened * 90;

  return Math.max(-90, Math.min(90, angle));
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
      <TeamScore teamName="JAKE"/>
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 423 230" width="423" height="230" className="border">
        <g>
          <path d="M211.5,8C324.031,8 407.207,83.639 422.832,196.17 C428.282,235.425 324.031,204.803 211.5,204.803 C98.969,204.803 -5.282,235.425 0.168,196.17 C15.793,83.639 98.969,8 211.5,8 Z" fill="#fff" stroke="#000" strokeWidth="1"/>
          <path d="M211.5,179.04C227.488,179.04 240.474,192.026 240.474,208.014 C240.474,224.001 227.488,236.988 211.5,236.988 C195.513,236.988 182.526,224.001 182.526,208.014 C182.526,192.026 195.513,179.04 211.5,179.04 Z" fill="#000" stroke="#000" strokeWidth="2"/>
          <path className="gauge-needle" d="M205.11,205.491L208.5,31.392 C208.5,31.392 214.89,205.639 214.89,205.639 Z" fill="#000" stroke="#000" strokeWidth="2" transform={`rotate(${needleAngle} 211.5 208)`}/>
        </g>
      </svg>
      <TeamScore teamName="BADCOP"/>
    </div>
  )
}
