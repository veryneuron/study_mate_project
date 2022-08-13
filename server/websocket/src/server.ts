import { WebSocket, WebSocketServer } from 'ws';
import { init } from './init';

init(new WebSocketServer({ port: 8080 }), new Map<WebSocket, string>());
