import { verify } from 'jsonwebtoken';
import { WebSocket, WebSocketServer } from 'ws';
import connData from './connData';

export function middleWebsocket(
  wss: WebSocketServer,
  userMap: Map<WebSocket, string>
) {
  wss.on('connection', function (ws, req) {
    console.log('Start verifying token');
    try {
      const token = verify(
        decodeURI(<string>req.url?.split('/')[2])
          .replace('{', '')
          .replace('}', '') ?? '',
        process.env.JWT_SECRET ?? ''
      );
      console.log(`User connected: ${token.sub}`);

      userMap.forEach((value) => {
        ws.send(JSON.stringify(new connData(value, 'Connected')));
      });

      userMap.set(ws, <string>token.sub);

      wss.clients.forEach((client) => {
        if (client.readyState === client.OPEN && client !== ws) {
          client.send(
            JSON.stringify(new connData(<string>token.sub, 'Connected'))
          );
        }
      });
    } catch (err) {
      ws.send(JSON.stringify(new connData('', 'Unauthorized')));
      ws.close();
    }
    ws.on('close', function (num) {
      if (userMap.has(this)) {
        wss.clients.forEach((ws) => {
          if (ws.readyState === ws.OPEN) {
            const test = new connData(userMap.get(this), 'Disconnected');
            ws.send(JSON.stringify(test));
          }
        });
        userMap.delete(this);
      }
      console.log(`User disconnected : ${userMap.get(this) ?? ''} - ${num}`);
    });
    ws.on('error', function (err) {
      console.log(err);
    });
  });
}
