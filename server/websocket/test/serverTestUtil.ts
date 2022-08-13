import { WebSocket, WebSocketServer } from 'ws';
import { connect } from 'mqtt';
import { init } from '../src/init';
import { Collection, MongoClient } from 'mongodb';

export const collections: { chattingData?: Collection } = {};

async function startTestServer(port: number) {
  const dbClient = await MongoClient.connect(
    `mongodb://${process.env.POSTGRES_ID}:${process.env.POSTGRES_PW}@localhost:27017`
  );
  const db = dbClient.db('study_mate_test');
  collections.chattingData = db.collection('chatting_data');

  const wss = new WebSocketServer({ port: port });
  const client = connect('mqtt://localhost:1996');
  const userMap = new Map<WebSocket, string>();
  init(wss, client, userMap);

  return { websocket: wss, mqtt: client, dbClient };
}

function waitForSocketState(socket: WebSocket, state: number) {
  return new Promise<void>(function (resolve) {
    setTimeout(function () {
      if (socket.readyState === state) {
        resolve();
      } else {
        waitForSocketState(socket, state).then(resolve);
      }
    }, 5);
  });
}

export { startTestServer, waitForSocketState };
