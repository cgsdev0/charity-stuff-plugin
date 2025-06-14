import { create } from "zustand";
import { produce } from "immer";
import { combine } from "zustand/middleware";

type Magic<T extends (...args: any) => any> = Exclude<Parameters<T>[0], (...args: any) => any>;

export interface Player {
  name: string;
  uuid: string;
}

export interface Team {
  leader: "JAKE" | "BADCOP";
  score: number;
  players: string[]; // UUID only
  objectives: Array<{
    value: {
      unlockedBy: string;
      unlockedAt: string;
    };
    key: {
      key: string;
      player: string; // UUID or empty string
    };
  }>;
}

export interface Objective {
  advancement: string;
  kind: "PER_TEAM" | "PER_PLAYER";
  name: string;
  key: string;
  worth: number;
}

export const useStore = create(
  combine(
    {
      players: [] as Player[],
      teams: [] as Team[],
      objectives: [] as Objective[],
    },
    (set) => ({
      setKey: <T extends keyof Magic<typeof set>>(key: T, data: Magic<typeof set>[T]) =>
        set(
          produce((state) => {
            state[key] = data;
          }),
        ),
    }),
  ),
);
