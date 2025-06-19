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
  players: string[]; // UUIDs
  objectives: Array<{
    value: {
      unlockedBy: string; // UUID
      unlockedAt: string;
    };
    key: {
      key: string;
      player?: string; // UUID
    };
  }>;
}

export interface Objective {
  advancement: string;
  kind: "PER_TEAM" | "PER_PLAYER";
  name: string;
  key: string;
  worth: number;
  description?: string;
}

export interface Stream {
  user_id: string;
  user_login: string;
  display_name: string;
  profile_image_url: string;
}

export const useStore = create(
  combine(
    {
      players: {} as Record<string, string>,
      teams: [] as Team[],
      objectives: {} as Record<string, Objective>,
      streams: [] as Stream[],
    },
    (set) => ({
      setKey: (key: keyof Magic<typeof set>, data: any) =>
        set(
          produce((state) => {
            switch (key) {
              case "players":
                (data as Player[]).forEach((player) => {
                  state.players[player.uuid] = player.name;
                });
                break;
              case "objectives":
                (data as Objective[]).forEach((obj) => {
                  state.objectives[obj.key] = obj;
                });
                break;
              default:
                state[key] = data;
            }
          }),
        ),
    }),
  ),
);
