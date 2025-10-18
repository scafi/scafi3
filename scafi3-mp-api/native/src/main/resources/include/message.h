#ifndef MESSAGE_H
#define MESSAGE_H

#include <inttypes.h>
#include <stdbool.h>
#include <stddef.h>

typedef struct BinaryCodable {
    char* type_name;
    const uint8_t* (*encode)(const void *data, size_t *encoded_size);
    const void* (*decode)(const uint8_t *buffer, size_t size);
    bool (*cmp)(const void* a, const void* b);
    uint32_t (*hash)(const void* data);
    char* (*to_str)(const void* data);
} BinaryCodable;

#endif // MESSAGE_H