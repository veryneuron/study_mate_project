import { WebSocket, WebSocketServer } from 'ws';
import { connectToDatabase } from './database/chattingConnector';
import { connect } from 'mqtt';
import { init } from './init';

connectToDatabase().catch((err) => {
  console.log(err);
  process.exit();
});

const wss = new WebSocketServer({ port: 8080 });
const client = connect('mqtt://localhost:1996');
const userMap = new Map<WebSocket, string>();

init(wss, client, userMap);
