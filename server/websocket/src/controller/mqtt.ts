import { collections } from '../database/chattingConnector';
import ChattingData from '../database/chattingData';
import { WebSocketServer } from 'ws';
import { MqttClient } from 'mqtt';

export function middleMqtt(wss: WebSocketServer, client: MqttClient) {
  client.on('connect', function () {
    client.subscribe('chatting_data');
    console.log('subscribed to chatting_data');
    client.subscribe('study_record');
    console.log('subscribed to study_record');
  });

  client.on('message', async function (topic, message) {
    if (topic === 'chatting_data') {
      console.log('chatting_data received:' + message.toString());
      const receiveData: ChattingData = JSON.parse(message.toString());
      receiveData.type = 'message';
      try {
        const result = await collections.chattingData?.insertOne(receiveData);
        receiveData._id = result?.insertedId;
        wss.clients.forEach(function each(ws) {
          if (ws.readyState === ws.OPEN) {
            ws.send(JSON.stringify(receiveData));
          }
        });
      } catch (err) {
        console.log(err);
      }
    } else if (topic === 'study_record') {
      console.log('study_record received:' + message.toString());
      const userIdValue: string = JSON.parse(message.toString()).userId;
      wss.clients.forEach(function each(ws) {
        if (ws.readyState === ws.OPEN) {
          ws.send(
            new ChattingData(
              userIdValue,
              message.toString(),
              '',
              new Date(),
              'study_record'
            )
          );
        }
      });
    }
  });

  client.on('error', function (err) {
    console.log(err);
  });
}
