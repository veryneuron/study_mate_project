import { WebSocket, WebSocketServer } from 'ws';
import { MqttClient } from 'mqtt';
import { middleWebsocket } from './controller/websocket';
import { middleMqtt } from './controller/mqtt';

export function init(
  wss: WebSocketServer,
  client: MqttClient,
  userMap: Map<WebSocket, string>
) {
  middleWebsocket(wss, userMap);
  middleMqtt(wss, client);
}
