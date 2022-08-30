from awsiot import mqtt_connection_builder


def build_mqtt_connection():
    return mqtt_connection_builder.mtls_from_path(
        endpoint="endpoint",
        cliend_id="client_id",
        ca_filepath="ca_filepath",
        cert_filepath="cert_filepath",
        pri_key_filepath="pri_key_filepath")
