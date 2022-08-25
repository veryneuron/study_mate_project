FROM node:lts-slim
ENV NODE_ENV=production
WORKDIR /usr/src/app
COPY build/ .
COPY websocket_server_credential ./websocket_server_credential
COPY package.json .
COPY package-lock.json .
RUN npm install

ENTRYPOINT [ "node", "server.js" ]