import WebSocket, { Server } from 'ws';
import { startTestServer, waitForSocketState } from './serverTestUtil';
import connData from '../src/controller/connData';
import { mqtt } from 'aws-iot-device-sdk-v2';

const port = 8080;

const testToken =
  'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0YWNjb3VudCIsImlzcyI6InN0dWR5IG1hdGUgYXV0aCBzZXJ2ZXIiLCJpYXQiOjE2NjAxODk0MTcsImV4cCI6MTY2MTkxNzQxN30.zWAxLLkRLa_xNSIlKwcfrfGd3ogHmUj-JbpeRME1kJA';
// until 2022-08-31
// userId = 'testaccount';

describe('websocket test', function () {
  let wss: Server;
  let connection: mqtt.MqttClientConnection;
  beforeAll(async () => {
    [wss, connection] = await startTestServer(port);
  });

  afterAll(() => {
    wss.close();
    connection.disconnect();
  });

  test('connect test with jwt token', async () => {
    const wsClient = new WebSocket(`ws://localhost:${port}/${testToken}`);
    await waitForSocketState(wsClient, WebSocket.OPEN);
    expect(wsClient.readyState).toBe(WebSocket.OPEN);
    wsClient.close();
    await waitForSocketState(wsClient, WebSocket.CLOSED);
  });

  test('connect test without jwt token', async () => {
    const wsClient = new WebSocket(`ws://localhost:${port}`);
    await waitForSocketState(wsClient, WebSocket.CLOSED);
    expect(wsClient.readyState).toBe(WebSocket.CLOSED);
  });

  test('several user connection test', async () => {
    const wsClient = new WebSocket(`ws://localhost:${port}/${testToken}`);
    await waitForSocketState(wsClient, WebSocket.OPEN);
    wsClient.on('message', (data) => {
      const receiveData = JSON.parse(data.toString()) as connData;
      expect(receiveData.userId).toBe('testaccount');
      expect(receiveData.type).toBe('Connected');
      wsClient.close();
    });

    const wsClient2 = new WebSocket(`ws://localhost:${port}/${testToken}`);
    await waitForSocketState(wsClient2, WebSocket.OPEN);
    await waitForSocketState(wsClient, WebSocket.CLOSED);
    wsClient2.close();
    await waitForSocketState(wsClient2, WebSocket.CLOSED);
  });

  test('connection close broadcasting test', async () => {
    const wsClient = new WebSocket(`ws://localhost:${port}/${testToken}`);
    await waitForSocketState(wsClient, WebSocket.OPEN);
    const wsClient2 = new WebSocket(`ws://localhost:${port}/${testToken}`);
    await waitForSocketState(wsClient2, WebSocket.OPEN);

    wsClient.on('message', (data) => {
      const receiveData = JSON.parse(data.toString()) as connData;
      expect(receiveData.userId).toBe('testaccount');
      expect(receiveData.type).toBe('Disconnected');
      wsClient.close();
    });
    wsClient2.close();

    await waitForSocketState(wsClient2, WebSocket.CLOSED);
    await waitForSocketState(wsClient, WebSocket.CLOSED);
  });

  test('get existing user information test', async () => {
    const wsClient = new WebSocket(`ws://localhost:${port}/${testToken}`);
    await waitForSocketState(wsClient, WebSocket.OPEN);

    const wsClient2 = new WebSocket(`ws://localhost:${port}/${testToken}`);
    wsClient2.on('message', (data) => {
      const receiveData = JSON.parse(data.toString()) as connData;
      expect(receiveData.userId).toBe('testaccount');
      expect(receiveData.type).toBe('Connected');
      wsClient2.close();
    });
    await waitForSocketState(wsClient2, WebSocket.CLOSED);
    wsClient.close();
    await waitForSocketState(wsClient, WebSocket.CLOSED);
  });
});
