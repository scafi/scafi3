#ifndef INT_CODABLE_H
#define INT_CODABLE_H

#include <stdint.h>
#include <stddef.h>
#include <stdbool.h>
#include <message.h>

typedef struct Int {
    BinaryCodable base;
    int value;
} Int;

BinaryCodable* int_of(int value);
void int_free(Int* iv);

#define device(x) _Generic((x), \
    int: int_of                 \
)(x)

#endif