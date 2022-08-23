import WebSocket, { Server } from 'ws';
import { startTestServer, waitForSocketState } from './serverTestUtil';
import connData from '../src/controller/connData';
import { mqtt } from 'aws-iot-device-sdk-v2';
import mqttData from '../src/controller/mqttData';

const port = 8080;

const testToken =
  'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0YWNjb3VudCIsImlzcyI6InN0dWR5IG1hdGUgYXV0aCBzZXJ2ZXIiLCJpYXQiOjE2NjAxODk0MTcsImV4cCI6MTY2MTkxNzQxN30.zWAxLLkRLa_xNSIlKwcfrfGd3ogHmUj-JbpeRME1kJA';
// until 2022-08-31
// userId = 'testaccount';

describe('AWS IoT Mqtt connection test', function () {
  let wss: Server;
  let connection: mqtt.MqttClientConnection;
  beforeEach(async () => {
    [wss, connection] = await startTestServer(port);
  });

  afterEach(() => {
    wss.close();
    connection.disconnect();
  });

  test('studyTime receive test', async () => {
    const wsClient = new WebSocket(`ws://localhost:${port}/${testToken}`);
    await waitForSocketState(wsClient, WebSocket.OPEN);
    wsClient.on('message', (data) => {
      const receiveData = JSON.parse(data.toString()) as connData;
      expect(receiveData.userId).toBe('testaccount');
      expect(receiveData.type).toBe('StudyTime');
      wsClient.close();
    });
    const payload = {} as mqttData;
    payload.userId = 'testaccount';
    payload.startTimestamp = new Date(Date.now());
    payload.endTimestamp = null;
    await connection.publish(
      'study_time',
      JSON.stringify(payload),
      mqtt.QoS.AtLeastOnce
    );
    await waitForSocketState(wsClient, WebSocket.CLOSED);
  });

  test('studyRecord receive test', async () => {
    const wsClient = new WebSocket(`ws://localhost:${port}/${testToken}`);
    await waitForSocketState(wsClient, WebSocket.OPEN);
    wsClient.on('message', (data) => {
      const receiveData = JSON.parse(data.toString()) as connData;
      expect(receiveData.userId).toBe('testaccount');
      expect(receiveData.type).toBe('StudyRecord');
      wsClient.close();
    });
    const payload = {} as mqttData;
    payload.userId = 'testaccount';
    payload.startTimestamp = new Date(Date.now());
    payload.endTimestamp = null;
    await connection.publish(
      'study_record',
      JSON.stringify(payload),
      mqtt.QoS.AtLeastOnce
    );
    await waitForSocketState(wsClient, WebSocket.CLOSED);
  });
});
