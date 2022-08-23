import { WebSocket, WebSocketServer } from 'ws';
import { init } from '../src/init';
import { build_connection } from '../src/AWSConnection';

async function startTestServer(port: number) {
  const wss = new WebSocketServer({ port: port });
  const connection = build_connection();
  await connection.connect();
  console.log('AWS Iot connected!');
  init(wss, connection, new Map<WebSocket, string>());

  return [wss, connection] as const;
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
