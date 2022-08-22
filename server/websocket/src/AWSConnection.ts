import { iot, mqtt } from 'aws-iot-device-sdk-v2';

export function build_connection(): mqtt.MqttClientConnection {
  const cert = '../../../websocket_server_credential';
  const key = '../../../websocket_server_credential';
  const ca = '../../../websocket_server_credential';

  const config =
    iot.AwsIotMqttConnectionConfigBuilder.new_mtls_builder_from_path(cert, key)
      .with_certificate_authority_from_path(undefined, ca)
      .with_clean_session(true)
      .with_client_id('websocket_server')
      .with_endpoint('a1qx0jq8qjq8q0.iot.us-east-1.amazonaws.com')
      .build();

  const client = new mqtt.MqttClient();
  return client.new_connection(config);
}
