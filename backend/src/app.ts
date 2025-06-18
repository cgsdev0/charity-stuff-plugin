import Fastify from "fastify";
import { throttle } from "throttle-debounce";
import { parse } from "yaml";
import { readFile, watch } from "fs";
import websocket from "@fastify/websocket";
import { EventEmitter } from "events";
import "dotenv/config";
import users from "../users.json" with { type: "json" };

class UpdateEmitter extends EventEmitter {}

const updateEmitter = new UpdateEmitter();

const fastify = Fastify({
  logger: true,
});

await fastify.register(websocket);

fastify.get("/ws", { websocket: true }, function wsHandler(socket, req) {
  const onUpdate = (update: string) => {
    socket.send(update);
  };
  socket.send(JSON.stringify(objectives));
  socket.send(JSON.stringify(players));
  socket.send(JSON.stringify(db));
  socket.send(twitchJson);
  updateEmitter.on("update", onUpdate);

  socket.on("close", () => {
    updateEmitter.off("update", onUpdate);
  });
});

let db: any = {};
let objectives: any = {};
let players: any = {};

const rootDir = process.env.ROOT_DIR || "/home/sarah/minecraft-plugins/charity-stuff-plugin/run";
const dataSource = `${rootDir}/plugins/charity-stuff-plugin/data.yml`;
const objectiveSource = `${rootDir}/plugins/charity-stuff-plugin/objectives.yml`;
const playerSource = `${rootDir}/usercache.json`;

let oldPlayers = "";
const updatePlayers = () => {
  readFile(playerSource, (err, data) => {
    if (!err) {
      const p = JSON.parse(data.toString());
      players = { type: "players", data: p.map(({ expiresOn, ...rest }) => rest) };
      const newPlayers = JSON.stringify(players);
      // length is a perfectly acceptable checksum in many cultures
      if (oldPlayers.length !== newPlayers.length) {
        updateEmitter.emit("update", newPlayers);
        oldPlayers = newPlayers;
      }
    } else {
      console.error(err);
    }
  });
};

const updateData = () => {
  readFile(dataSource, (err, data) => {
    if (!err) {
      const parsed = parse(data.toString());
      db = { type: "teams", data: parsed.teams.map(({ ["=="]: _, ...rest }) => rest) };
      updateEmitter.emit("update", JSON.stringify(db));
    } else {
      console.error(err);
    }
  });
};

const updateObjectives = () => {
  readFile(objectiveSource, (err, data) => {
    if (!err) {
      const parsed = parse(data.toString());
      objectives = { type: "objectives", data: parsed.objectives };
      updateEmitter.emit("update", JSON.stringify(objectives));
    } else {
      console.error(err);
    }
  });
};

const connectSource = (source: string, update: any) => {
  update();
  let throttled = throttle(1000, update, { noLeading: true });
  watch(source, (_, filename) => {
    if (filename) {
      throttled();
    }
  });
};

let twitch_token: string | undefined;
const refreshTokens = async () => {
  console.log("doing a refresh");
  const id = process.env.TWITCH_CLIENT_ID;
  const secret = process.env.TWITCH_CLIENT_SECRET;
  const resp = await fetch("https://id.twitch.tv/oauth2/token", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: new URLSearchParams({ client_id: id, client_secret: secret, grant_type: "client_credentials" }),
  });
  const { access_token, token_type } = await resp.json();
  twitch_token = access_token;
};

interface TwitchUser {
  user_id: string;
  user_login: string;
  display_name: string;
  profile_image_url: string;
  live: boolean;
}

const twitchCache: Record<string, TwitchUser> = {};

const queryTwitch = async (retry_count = 3) => {
  if (retry_count === 0) {
    return;
  }
  const id = process.env.TWITCH_CLIENT_ID;
  const userStr = "&" + users.map((user: string) => `user_login=${user}`).join("&");
  const resp = await fetch(`https://api.twitch.tv/helix/streams?type=live${userStr}`, { headers: { Authorization: `Bearer ${twitch_token}`, "Client-Id": id } });
  if (resp.status === 401) {
    await refreshTokens();
    return queryTwitch(retry_count - 1);
  }
  const data = await resp.json();
  Object.keys(twitchCache).forEach((key) => {
    twitchCache[key].live = false;
  });
  const live = data.data.map(({ user_id }) => user_id);
  const uncached = live.filter((user: string) => !twitchCache.hasOwnProperty(user));
  const cached = live.filter((user: string) => twitchCache.hasOwnProperty(user));
  cached.forEach((key: string) => {
    twitchCache[key].live = true;
  });
  if (uncached.length) {
    const userStr2 = uncached.map((user: string) => `id=${user}`).join("&");
    const resp2 = await fetch(`https://api.twitch.tv/helix/users?${userStr2}`, { headers: { Authorization: `Bearer ${twitch_token}`, "Client-Id": id } });
    const results = await resp2.json();
    results.data.forEach((result: any) => {
      twitchCache[result.id] = {
        user_id: result.id,
        user_login: result.login,
        display_name: result.display_name,
        profile_image_url: result.profile_image_url,
        live: true,
      };
    });
  }
};

let twitchJson = "";
async function twitchLoop() {
  await queryTwitch();
  const payload = {
    type: "streams",
    data: Object.values(twitchCache)
      .filter((o) => o.live)
      .map(({ live, ...rest }) => rest),
  };
  const newJson = JSON.stringify(payload);
  if (newJson !== twitchJson) {
    updateEmitter.emit("update", JSON.stringify(newJson));
    twitchJson = newJson;
  }
  setTimeout(twitchLoop, 60 * 1000);
}

twitchLoop();

connectSource(dataSource, updateData);
connectSource(playerSource, updatePlayers);
connectSource(objectiveSource, updateObjectives);

try {
  await fastify.listen({ port: 3009 });
} catch (err) {
  fastify.log.error(err);
  process.exit(1);
}
