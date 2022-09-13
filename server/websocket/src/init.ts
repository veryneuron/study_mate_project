import { WebSocket, WebSocketServer } from 'ws';
import { middleWebsocket } from './controller/websocket';
import { mqtt } from 'aws-iot-device-sdk-v2';
import { middleMqtt } from './controller/AWSMqtt';

export function init(
  wss: WebSocketServer,
  connection: mqtt.MqttClientConnection,
  userMap: Map<WebSocket, string>
) {
  middleWebsocket(wss, userMap);
  middleMqtt(connection, userMap);
}
