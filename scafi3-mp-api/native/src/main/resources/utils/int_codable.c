#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include "int_codable.h"

static void init_int_value(Int* iv, int value);

static const uint8_t* int_value_encode(const void *data, size_t *encoded_size) {
    char buffer[32];
    int len = snprintf(buffer, sizeof(buffer), "%d", ((Int*)data)->value);
    uint8_t* encoded = malloc(len);
    if (encoded) {
        memcpy(encoded, buffer, len);
        *encoded_size = len;
    }
    return encoded;
}

static bool int_value_cmp(const void* a, const void* b) {
    return a && b && ((Int*)a)->value == ((Int*)b)->value;
}

static uint32_t int_value_hash(const void* data) {
    if (!data) return 0;
    uint32_t hash = (uint32_t)((Int*)data)->value;
    hash = (hash >> 16 ^ hash) * 0x45d9f3b;
    hash = (hash >> 16 ^ hash) * 0x45d9f3b;
    return hash >> 16 ^ hash;
}

static char* int_value_to_str(const void* data) {
    if (!data) return NULL;
    char* str = malloc(32);
    if (str) snprintf(str, 32, "%d", ((Int*)data)->value);
    return str;
}

static const void* int_value_decode(const uint8_t *buffer, size_t size) {
    if (!buffer || size == 0) return NULL;
    char* str = malloc(size + 1);
    if (!str) return NULL;
    memcpy(str, buffer, size);
    str[size] = '\0';
    char* endptr;
    long parsed_value = strtol(str, &endptr, 10);
    if (endptr == str || *endptr != '\0') {
        fprintf(stderr, "ERROR: Failed to parse '%s'\n", str);
        free(str);
        return NULL;
    }
    free(str);
    Int* iv = malloc(sizeof(Int));
    if (iv) init_int_value(iv, (int)parsed_value);
    return iv;
}

static void init_int_value(Int* iv, int value) {
    iv->base.type_name = "number";
    iv->base.encode = int_value_encode;
    iv->base.decode = int_value_decode;
    iv->base.eq.cmp = int_value_cmp;
    iv->base.eq.hash = int_value_hash;
    iv->base.to_str = int_value_to_str;
    iv->value = value;
}

BinaryCodable* int_of(int value) {
    Int* iv = malloc(sizeof(Int));
    if (iv) init_int_value(iv, value);
    return (BinaryCodable*)iv;
}

void int_free(Int* iv) {
    free(iv);
}
