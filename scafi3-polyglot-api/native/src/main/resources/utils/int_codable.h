#ifndef INT_CODABLE_H
#define INT_CODABLE_H

#include <stdint.h>
#include <stddef.h>
#include <stdbool.h>
#include <message.h>

/**
 * A structure representing an integer value as a binary codable.
 */
typedef struct Int {
    BinaryCodable base;
    int value;
} Int;

/**
 * Creates a new `Int` instance from the given integer value.
 * @param value the integer value
 * @return a pointer to the newly created `Int` instance
 */
BinaryCodable* int_of(int value);

/**
 * Frees the memory allocated for the given `Int` instance.
 * @param iv a pointer to the `Int` instance to free
 */
void int_free(Int* iv);

/**
 * A macro to create a device identifier from an integer value.
 * @param x the integer value
 * @return a pointer to the created `BinaryCodable` instance
 */
#define device(x) _Generic((x), \
    int: int_of                 \
)(x)

#endif