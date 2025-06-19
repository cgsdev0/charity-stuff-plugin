import NumberFlow from "@number-flow/react";

export function TeamScore({ score, side }: { score: number; side: "left" | "right" }) {
  return <NumberFlow value={score} className={`team-score text-${side}`} />;
}
