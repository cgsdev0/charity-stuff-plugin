import Fastify from "fastify";
import { throttle } from "throttle-debounce";
import { parse } from "yaml";
import { readFile, watch } from "fs";
import websocket from "@fastify/websocket";
import { EventEmitter } from "events";

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
  updateEmitter.on("update", onUpdate);

  socket.on("close", () => {
    updateEmitter.off("update", onUpdate);
  });
});

let db: any = {};
let objectives: any = {};
let players: any = {};

const rootDir = "/home/sarah/minecraft-plugins/charity-stuff-plugin/run";
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
      objectives = { type: "objectives", data: parsed };
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

connectSource(dataSource, updateData);
connectSource(playerSource, updatePlayers);
connectSource(objectiveSource, updateObjectives);

try {
  await fastify.listen({ port: 3009 });
} catch (err) {
  fastify.log.error(err);
  process.exit(1);
}
