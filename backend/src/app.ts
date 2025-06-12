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
  socket.send(JSON.stringify(db));
  updateEmitter.on("update", onUpdate);

  socket.on("close", () => {
    updateEmitter.off("update", onUpdate);
  });
});

let db = {};

const dataSource = "/home/sarah/minecraft-plugins/charity-stuff-plugin/run/plugins/charity-stuff-plugin/data.yml";

const updateDB = throttle(
  1000,
  (initial: boolean) => {
    console.log(`refreshing DB`);
    readFile(dataSource, (err, data) => {
      if (!err) {
        db = parse(data.toString());
        updateEmitter.emit("update", JSON.stringify(db));
        if (initial) {
          console.log(db);
        }
      } else {
        console.error(err);
      }
    });
  },
  { noLeading: true },
);

updateDB(true);
watch(dataSource, (event, filename) => {
  if (filename) {
    updateDB(false);
  }
});

try {
  await fastify.listen({ port: 3009 });
} catch (err) {
  fastify.log.error(err);
  process.exit(1);
}
