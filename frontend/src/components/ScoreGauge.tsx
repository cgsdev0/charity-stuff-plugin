import {TeamScore} from "./TeamScore.tsx";

export function ScoreGauge() {
  return (
    <>
      <TeamScore teamName="JAKE"/>
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 423 230" width="423" height="230" className="border">
        <g>
          <path d="M211.5,8C324.031,8 407.207,83.639 422.832,196.17 C428.282,235.425 324.031,204.803 211.5,204.803 C98.969,204.803 -5.282,235.425 0.168,196.17 C15.793,83.639 98.969,8 211.5,8 Z" fill="#fff" stroke="#000" strokeWidth="1"/>
          <path d="M211.5,179.04C227.488,179.04 240.474,192.026 240.474,208.014 C240.474,224.001 227.488,236.988 211.5,236.988 C195.513,236.988 182.526,224.001 182.526,208.014 C182.526,192.026 195.513,179.04 211.5,179.04 Z" fill="#000" stroke="#000" strokeWidth="2"/>
          <path d="M205.11,205.491L208.5,31.392 C208.5,31.392 214.89,205.639 214.89,205.639 Z" fill="#000" stroke="#000" strokeWidth="2"/>
        </g>
      </svg>
      <TeamScore teamName="BADCOP"/>
    </>
  )
}
