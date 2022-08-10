import { collections } from '../database/chattingService';
import ChattingData from '../database/chatting_data';
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
      try {
        const result = await collections.chattingData?.insertOne(
          JSON.parse(message.toString()) as ChattingData
        );
        wss.clients.forEach(function each(ws) {
          if (ws.readyState === ws.OPEN) {
            const temp: ChattingData = JSON.parse(message.toString());
            temp._id = result?.insertedId;
            ws.send(JSON.stringify(JSON.stringify(temp)));
          }
        });
      } catch (err) {
        console.log(err);
      }
    } else if (topic === 'study_record') {
      console.log('study_record received:' + message.toString());
      wss.clients.forEach(function each(ws) {
        if (ws.readyState === ws.OPEN) {
          ws.send(message.toString());
        }
      });
    }
  });

  client.on('error', function (err) {
    console.log(err);
  });
}
