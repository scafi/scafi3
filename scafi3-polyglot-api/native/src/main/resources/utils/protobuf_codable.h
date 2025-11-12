#ifndef PROTOBUF_CODABLE_H
#define PROTOBUF_CODABLE_H

#include <stdint.h>
#include <stddef.h>
#include <message.h>
#include "protobuf-c/protobuf-c.h"

/**
 * A structure representing a Protobuf message as a binary codable.
 */
typedef struct ProtobufValue {
    BinaryCodable base;
    ProtobufCMessage* message;
    const ProtobufCMessageDescriptor* descriptor;
} ProtobufValue;

/**
 * Generate decode function for a protobuf message type.
 * @param type_name The protobuf-c prefix (e.g., temperature_sensor)
 * @param to_str_func Custom to_str function, or NULL for a default high-level one
 */
#define DEFINE_PROTOBUF_MESSAGE(type_name, to_str_func)                                                                \
    static const signed char* (*type_name##_to_str_ptr)(const void*) = to_str_func;                                    \
    static const void* decode_##type_name(const uint8_t* buffer, size_t size) {                                        \
        if (!buffer) return NULL;                                                                                      \
        void* msg = NULL;                                                                                              \
        if (size == 0) {                                                                                               \
            /* Empty buffer means all fields have default values */                                                    \
            msg = calloc(1, type_name##__descriptor.sizeof_message);                                                   \
            if (msg) {                                                                                                 \
                ((ProtobufCMessage*)msg)->descriptor = &type_name##__descriptor;                                       \
            }                                                                                                          \
        } else {                                                                                                       \
            msg = type_name##__unpack(NULL, size, buffer);                                                             \
        }                                                                                                              \
        return msg ? protobuf_value_create((ProtobufCMessage*)msg, decode_##type_name, type_name##_to_str_ptr) : NULL; \
    }

/**
 * Create a ProtobufValue from a protobuf message.
 * @param msg_ptr Pointer to the protobuf message
 * @param type_name The protobuf-c prefix (must match DEFINE_PROTOBUF_MESSAGE)
 */
#define proto_of(msg_ptr, type_name) \
    protobuf_value_create(&(msg_ptr)->base, decode_##type_name, type_name##_to_str_ptr)

/**
 * Creates a new `ProtobufValue` instance.
 * @param message pointer to the protobuf message
 * @param decode_func function to decode the protobuf message
 * @param to_str_func function to convert the protobuf message to a string
 * @return a pointer to the newly created `ProtobufValue` instance
 */
ProtobufValue* protobuf_value_create(
    ProtobufCMessage* message,
    const void* (*decode_func)(const uint8_t*, size_t),
    const signed char* (*to_str_func)(const void*)
);

/**
 * Default string representation function for ProtobufValue.
 * @param data pointer to the ProtobufValue instance
 * @return pointer to the string representation
 */
const signed char* protobuf_default_to_str(const void* data);

/**
 * Frees the memory allocated for the given `ProtobufValue` instance.
 */
void protobuf_value_free(ProtobufValue* pv);

#endif