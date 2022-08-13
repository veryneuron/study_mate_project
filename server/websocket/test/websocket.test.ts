import { MqttClient } from 'mqtt';
import WebSocket, { Server } from 'ws';
import {
  collections,
  startTestServer,
  waitForSocketState
} from './serverTestUtil';
import ChattingData from '../src/database/chattingData';
import { MongoClient } from 'mongodb';

const port = 8080;

const testToken =
  'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0YWNjb3VudCIsImlzcyI6InN0dWR5IG1hdGUgYXV0aCBzZXJ2ZXIiLCJpYXQiOjE2NjAxODk0MTcsImV4cCI6MTY2MTkxNzQxN30.zWAxLLkRLa_xNSIlKwcfrfGd3ogHmUj-JbpeRME1kJA';
// until 2022-08-31
// userId = 'testaccount';

describe('websocket test', function () {
  let server: {
    websocket: Server;
    mqtt: MqttClient;
    dbClient: MongoClient;
  };

  beforeAll(async () => {
    server = await startTestServer(port);
  });

  afterAll(async () => {
    server.mqtt.end();
    server.websocket.close();
    await server.dbClient.close();
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
      const receiveData = JSON.parse(data.toString()) as ChattingData;
      expect(receiveData.userId).toBe('testaccount');
      expect(receiveData.type).toBe('connection');
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
      const receiveData = JSON.parse(data.toString()) as ChattingData;
      expect(receiveData.userId).toBe('testaccount');
      expect(receiveData.type).toBe('close');
      wsClient.close();
    });
    wsClient2.close();

    await waitForSocketState(wsClient2, WebSocket.CLOSED);
    await waitForSocketState(wsClient, WebSocket.CLOSED);
  });

  // test('initialize message test', async () => {
  //   await collections?.chattingData?.deleteMany({});
  //   await collections.chattingData?.insertOne(
  //     new ChattingData(
  //       'testaccount3',
  //       'test message3',
  //       '111.111',
  //       new Date('2022-08-10T03:24:00.000Z'),
  //       'message'
  //     )
  //   );
  //   await collections.chattingData?.insertOne(
  //     new ChattingData(
  //       'testaccount4',
  //       'test message4',
  //       '111.111',
  //       new Date(),
  //       'message'
  //     )
  //   );
  //
  //   const count = await collections.chattingData?.countDocuments();
  //   expect(count).toBe(2);
  //
  //   let flag = false;
  //   const wsClient = new WebSocket(`ws://localhost:${port}/${testToken}`);
  //   wsClient.on('message', (data) => {
  //     console.log(data.toString());
  //     const receiveMessage = JSON.parse(data.toString()) as ChattingData;
  //     if (flag === false) {
  //       expect(receiveMessage.userId).toBe('testaccount3');
  //       expect(receiveMessage.type).toBe('message');
  //       flag = true;
  //     } else {
  //       expect(receiveMessage.userId).toBe('testaccount4');
  //       expect(receiveMessage.type).toBe('message');
  //       wsClient.close();
  //     }
  //   });
  //
  //   await collections?.chattingData?.deleteMany({});
  //   await waitForSocketState(wsClient, WebSocket.CLOSED);
  // });

  test('get existing user information test', async () => {
    await collections?.chattingData?.deleteMany({});
    const wsClient = new WebSocket(`ws://localhost:${port}/${testToken}`);
    await waitForSocketState(wsClient, WebSocket.OPEN);

    const wsClient2 = new WebSocket(`ws://localhost:${port}/${testToken}`);
    wsClient2.on('message', (data) => {
      const receiveData = JSON.parse(data.toString()) as ChattingData;
      expect(receiveData.userId).toBe('testaccount');
      expect(receiveData.type).toBe('connection');
      wsClient2.close();
    });
    await waitForSocketState(wsClient2, WebSocket.CLOSED);
    wsClient.close();
    await waitForSocketState(wsClient, WebSocket.CLOSED);
  });
});
