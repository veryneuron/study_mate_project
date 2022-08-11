import { verify } from 'jsonwebtoken';
import { collections } from '../database/chattingService';
import ChattingData from '../database/chatting_data';
import { WebSocket, WebSocketServer } from 'ws';

export function middleWebsocket(
  wss: WebSocketServer,
  userMap: Map<WebSocket, string>
) {
  wss.on('connection', async function (ws, req) {
    try {
      const token = verify(
        req.url?.substring(1) ?? '',
        process.env.JWT_SECRET ?? ''
      );
      console.log(`User connected: ${token.sub}`);

      userMap.forEach((value) => {
        ws.send(
          JSON.stringify(
            new ChattingData(value, '', '', new Date(), 'connection')
          )
        );
      });

      userMap.set(ws, <string>token.sub);

      wss.clients.forEach(function each(wsClient) {
        if (wsClient.readyState === wsClient.OPEN && wsClient !== ws) {
          wsClient.send(
            JSON.stringify(
              new ChattingData(
                <string>token.sub,
                '',
                '',
                new Date(),
                'connection'
              )
            )
          );
        }
      });
    } catch (err) {
      const chatData = new ChattingData('', '', '', new Date(), 'unauthorized');
      ws.send(JSON.stringify(chatData));
      ws.close();
    }

    try {
      const chattingData = (await collections.chattingData
        ?.find({})
        .toArray()) as ChattingData[];
      ws.send(JSON.stringify(chattingData));
    } catch (err) {
      console.log(err);
    }
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
