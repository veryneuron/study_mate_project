import { WebSocketServer } from 'ws';
import { connectToDatabase } from './database/chattingService';
import { connect } from 'mqtt';
import { init } from './init';

connectToDatabase().catch((err) => {
  console.log(err);
  process.exit();
});

const wss = new WebSocketServer({ port: 8080 });
const client = connect('mqtt://localhost:1996');

init(wss, client);
