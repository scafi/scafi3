#include "messages.pb-c.h"
#include "protobuf_codable.c"

// Custom toString function for TemperatureSensor
const signed char* temperature_sensor_to_str(const void* data);

DEFINE_PROTOBUF_MESSAGE(temperature_sensor, temperature_sensor_to_str)

const ProtobufValue* sense_temperature() {
    TemperatureSensor* sensor = malloc(sizeof(TemperatureSensor));
    if (!sensor) return NULL;
    temperature_sensor__init(sensor);
    sensor->id = strdup("#{{ deviceId }}");
    sensor->temperature = {{ deviceId }} * 10.0f;
    return proto_of(sensor, temperature_sensor);
}

const void* aggregate_program(const AggregateLibrary* lang) {
    const BinaryCodable* temperature = (const BinaryCodable*) sense_temperature();
    return lang->neighbor_values(temperature);
}

const signed char* temperature_sensor_to_str(const void* data) {
    if (!data) return NULL;
    const ProtobufValue* pv = data;
    const TemperatureSensor* sensor = (const TemperatureSensor*)pv->message;
    const signed char* str = malloc(128);
    if (str) {
        snprintf((char*)str, 128, "Sensor{id=%s, temp=%.2f}", sensor->id, sensor->temperature);
    }
    return str;
}
