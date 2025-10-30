#ifndef SCAFI3_FIELD_H
#define SCAFI3_FIELD_H

#include "message.h"
#include "utils.h"

typedef BinaryCodable* DeviceId;

MAP_OF(NValues, DeviceId, BinaryCodable*)

typedef struct {
    const BinaryCodable* default_value;
    const NValues neighbor_values;
} Field;

/**
 * Converts a Field instance to a string representation.
 * @param field The Field instance to convert.
 * @return char* A string representation of the Field instance.
 * @note The returned string must be freed by the caller when no longer needed.
 */
char* field_to_str(const Field* field);

/**
 * Field-based shared data factory.
 */
typedef struct {
    /**
     * Creates a new field-based shared data instance.
     * @param default_value The default value for the field.
     * @return A pointer to the newly created Field instance.
     * @note The returned instance must be freed by the caller when no longer needed.
     */
    const Field* (*of)(const BinaryCodable* default_value);
} FieldBasedSharedData;

#endif // SCAFI3_FIELD_H
