import { iot, mqtt } from 'aws-iot-device-sdk-v2';

export function build_connection(): mqtt.MqttClientConnection {
  const cert = process.env.WS_AWS_CERT_PATH ?? '';
  const key = process.env.WS_AWS_KEY_PATH ?? '';
  const ca = process.env.WS_AWS_CERT_AUTH_PATH;
  const region = process.env.AWS_ENDPOINT ?? '';
  const clientId = process.env.WS_AWS_CLIENT_ID ?? '';

  const config =
    iot.AwsIotMqttConnectionConfigBuilder.new_mtls_builder_from_path(cert, key)
      .with_certificate_authority_from_path(undefined, ca)
      .with_clean_session(true)
      .with_client_id(clientId)
      .with_endpoint(region)
      .build();

  const client = new mqtt.MqttClient();
  return client.new_connection(config);
}
