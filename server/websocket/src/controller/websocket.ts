import { verify } from 'jsonwebtoken';
import { collections } from '../database/chattingService';
import ChattingData from '../database/chatting_data';
import { WebSocket, WebSocketServer } from 'ws';

export function middleWebsocket(
  wss: WebSocketServer,
  userMap: Map<WebSocket, string>
) {
  wss.on('connection', function (ws, req) {
    try {
      const token = verify(
        req.url?.substring(1) ?? '',
        process.env.JWT_SECRET ?? ''
      );
      userMap.set(ws, <string>token.sub);
      console.log(`User connected: ${token.sub}`);

      const enterRoom = new ChattingData(
        <string>token.sub,
        '',
        '',
        new Date(),
        'connection'
      );

      wss.clients.forEach(function each(ws) {
        if (ws.readyState === ws.OPEN) {
          ws.send(JSON.stringify(enterRoom));
        }
      });
    } catch (err) {
      const chatData = new ChattingData('', '', '', new Date(), 'unauthorized');
      ws.send(JSON.stringify(chatData));
      ws.close();
    }
    ws.on('open', async function () {
      userMap.forEach((value) => {
        const currentUser = new ChattingData(
          value,
          '',
          '',
          new Date(),
          'connection'
        );
        ws.send(JSON.stringify(currentUser));
      });
      try {
        const chattingData = (await collections.chattingData
          ?.find({})
          .toArray()) as ChattingData[];
        ws.send(JSON.stringify(chattingData));
      } catch (err) {
        console.log(err);
      }
    });
    ws.on('close', function (num) {
      const exitRoom = new ChattingData(
        userMap.get(this) ?? '',
        '',
        '',
        new Date(),
        'close'
      );
      wss.clients.forEach(function each(ws) {
        if (ws.readyState === ws.OPEN) {
          ws.send(JSON.stringify(exitRoom));
        }
      });
      console.log(`User disconnected : ${userMap.get(this) ?? ''} - ${num}`);
    });
    ws.on('error', function (err) {
      console.log(err);
    });
  });
}
