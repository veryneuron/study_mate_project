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
    } catch (err) {
      ws.send('Unauthorized');
      ws.close();
    }
    ws.on('open', async function () {
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
      console.log(`User disconnected : ${num}`);
    });
    ws.on('error', function (err) {
      console.log(err);
    });
  });
}
