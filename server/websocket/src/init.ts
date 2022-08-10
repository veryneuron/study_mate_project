import { WebSocketServer } from 'ws';
import { MqttClient } from 'mqtt';
import { middleWebsocket } from './controller/websocket';
import { middleMqtt } from './controller/mqtt';

export function init(wss: WebSocketServer, client: MqttClient) {
  middleWebsocket(wss);
  middleMqtt(wss, client);
}
