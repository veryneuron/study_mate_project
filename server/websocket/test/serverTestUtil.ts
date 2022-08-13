import { WebSocket, WebSocketServer } from 'ws';
import { init } from '../src/init';

function startTestServer(port: number) {
  const wss = new WebSocketServer({ port: port });
  init(wss, new Map<WebSocket, string>());

  return wss;
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
