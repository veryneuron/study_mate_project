import { WebSocket, WebSocketServer } from 'ws';
import { middleWebsocket } from './controller/websocket';

export function init(wss: WebSocketServer, userMap: Map<WebSocket, string>) {
  middleWebsocket(wss, userMap);
}
