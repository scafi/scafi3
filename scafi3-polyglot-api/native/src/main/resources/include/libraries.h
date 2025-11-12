#ifndef LIBRARIES_H
#define LIBRARIES_H

#include <stdbool.h>
#include "field.h"

/**
 * Aggregate library interface exposing core aggregate programming constructs and field-calculus constructs.
 */
typedef struct AggregateLibrary {
    /** Field-based factory accessor. */
    FieldBasedSharedData Field;

    /**
     * @return the local device identifier.
     */
    DeviceId (*local_id)(void);

    /**
     * Split the domain of the aggregate program into two branches.
     * @param condition the condition to be evaluated
     * @param true_branch the expression to be evaluated if the condition is true
     * @param false_branch the expression to be evaluated if the condition is false
     * @return the result of the expression that has been evaluated
     */
    const void* (*branch)(bool condition, const void* (*true_branch)(void), const void* (*false_branch)(void));

    /**
     * `evolve` repeatedly applies a function to an initial value for every execution round.
     * @param initial the initial value
     * @param evolution the function to be applied at each round
     * @return the value after the last application of the function
     */
    const void* (*evolve)(const void* initial, const void* (*evolution)(const void*));

    /**
     * `share` computes a value by repeatedly applying a function to an initial value while sharing the result with
     * neighbours.
     * @param initial the initial value
     * @param f the function that returns the value to share and return
     * @return the value after the last application of the function that has been shared with neighbours
     */
    const BinaryCodable* (*share)(const BinaryCodable* initial, const BinaryCodable* (*f)(const Field* field));

    /**
     * `neighborValues` sends a local value to neighbours and returns the aggregate value of the received messages.
     * @param value the local value to send to neighbours
     * @return the aggregate value of the received messages
     */
    const Field* (*neighbor_values)(const BinaryCodable* value);
} AggregateLibrary;

#endif // LIBRARIES_H
