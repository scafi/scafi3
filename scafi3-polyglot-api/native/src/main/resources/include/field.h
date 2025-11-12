#ifndef SCAFI3_FIELD_H
#define SCAFI3_FIELD_H

#include "message.h"
#include "utils.h"

/** 
 * A device identifier is represented as a generic binary codable value. 
 */
typedef BinaryCodable* DeviceId;

/** 
 * A mapping from device ids to binary codable values. 
 */
MAP_OF(NValues, DeviceId, BinaryCodable*)

/**
 * A Field (NValue in https://doi.org/10.1016/j.jss.2024.111976) is a mapping from device ids to values. For
 * devices not aligned with the current device, the default value is used.
 * @param default_value the default value for unaligned devices
 * @param neighbor_values the values for all devices, aligned and unaligned
 */
typedef struct {
    const BinaryCodable* default_value;
    const NValues neighbor_values;
} Field;

/**
 * Converts a Field instance to a string representation.
 * @param field The Field instance to print.
 * @return char* A string representation of the Field instance.
 * @note The returned string must be freed by the caller when no longer needed.
 */
char* field_to_str(const Field* field);

/**
 * Get a view of the aggregate value without the value of the "self" node.
 * @param field The Field instance.
 * @return an Array containing the neighbor values only, self value excluded.
 */
Array* without_self(const Field* field);

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
