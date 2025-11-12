#ifndef MESSAGE_H
#define MESSAGE_H

#include <inttypes.h>
#include <stdbool.h>
#include <stddef.h>

/**
 * A base structure for equality comparison and hashing.
 */
typedef struct Eq {
    /**
     * Compares two instances for equality.
     * @param a First instance.
     * @param b Second instance.
     * @return true if instances are equal, false otherwise.
     */
    bool (*cmp)(const void* a, const void* b);

    /**
     * Computes the hash code of an instance.
     * @param data The instance to hash.
     * @return The computed hash code.
     */
    uint32_t (*hash)(const void* data);
} Eq;

/**
 * A base structure for binary codable values, supporting encoding, decoding, string representation, 
 * and equality comparison.
 */
typedef struct BinaryCodable {
    /** Equality comparison and hashing functions. */
    Eq eq;

    /** 
     * Encodes the instance into a binary format.
     * @param data The instance to encode.
     * @param encoded_size Pointer to store the size of the encoded data.
     * @return Pointer to the encoded binary data.
     */
    const uint8_t* (*encode)(const void *data, size_t *encoded_size);

    /**
     * Decodes the binary data into an instance.
     * @param buffer The binary data buffer.
     * @param size The size of the binary data.
     * @return Pointer to the decoded instance.
     */
    const void* (*decode)(const uint8_t *buffer, size_t size);

    /**
     * Converts the instance to a string representation.
     * @param data The instance to convert.
     * @return Pointer to the string representation.
     */
    const signed char* (*to_str)(const void* data);
} BinaryCodable;

#endif // MESSAGE_H