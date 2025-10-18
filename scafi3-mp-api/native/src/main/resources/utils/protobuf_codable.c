#include "protobuf_codable.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static const uint8_t* protobuf_encode(const void *data, size_t *encoded_size) {
    const ProtobufValue* pv = data;
    if (!pv->message || !encoded_size) return NULL;
    *encoded_size = protobuf_c_message_get_packed_size(pv->message);
    uint8_t* buffer = malloc(*encoded_size);
    if (!buffer) return NULL;
    size_t packed = protobuf_c_message_pack(pv->message, buffer);
    if (packed != *encoded_size) {
        free(buffer);
        return NULL;
    }
    return buffer;
}

static bool protobuf_cmp(const void* a, const void* b) {
    if (!a || !b) return false;
    const ProtobufValue *pa = a, *pb = b;
    if (pa->descriptor != pb->descriptor) return false;
    size_t size_a, size_b;
    const uint8_t *buf_a = protobuf_encode(pa, &size_a);
    const uint8_t *buf_b = protobuf_encode(pb, &size_b);
    bool equal = buf_a && buf_b && size_a == size_b && memcmp(buf_a, buf_b, size_a) == 0;
    free((void*)buf_a);
    free((void*)buf_b);
    return equal;
}

static uint32_t protobuf_hash(const void* data) {
    if (!data) return 0;
    size_t size;
    const uint8_t* buf = protobuf_encode(data, &size);
    if (!buf) return 0;
    uint32_t hash = 2166136261u;
    for (size_t i = 0; i < size; i++) {
        hash ^= buf[i];
        hash *= 16777619u;
    }
    free((void*)buf);
    return hash;
}

char* protobuf_default_to_str(const void* data) {
    if (!data) return NULL;
    const ProtobufValue* pv = data;
    if (!pv->message) return NULL;
    const char* name = pv->message->descriptor->name;
    char* str = malloc(strlen(name) + 10);
    if (str) sprintf(str, "%s{...}", name);
    return str;
}

ProtobufValue* protobuf_value_create(
    ProtobufCMessage* message,
    const void* (*decode_func)(const uint8_t*, size_t),
    char* (*to_str_func)(const void*)
) {
    if (!message) return NULL;
    ProtobufValue* pv = malloc(sizeof(ProtobufValue));
    if (!pv) return NULL;
    pv->base.type_name = (char*)message->descriptor->name;
    pv->base.encode = protobuf_encode;
    pv->base.decode = decode_func;
    pv->base.cmp = protobuf_cmp;
    pv->base.hash = protobuf_hash;
    pv->base.to_str = to_str_func ? to_str_func : protobuf_default_to_str;
    pv->message = message;
    pv->descriptor = message->descriptor;
    return pv;
}

void protobuf_value_free(ProtobufValue* pv) {
    if (pv) {
        if (pv->message) {
            protobuf_c_message_free_unpacked(pv->message, NULL);
        }
        free(pv);
    }
}