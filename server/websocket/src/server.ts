import { WebSocket, WebSocketServer } from 'ws';
import { init } from './init';
import { build_connection } from './AWSConnection';

const connection = build_connection();
(async () => {
  await connection.connect();
})();

init(
  new WebSocketServer({ port: 8080 }),
  connection,
  new Map<WebSocket, string>()
);
