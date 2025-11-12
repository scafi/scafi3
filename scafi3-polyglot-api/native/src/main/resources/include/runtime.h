#ifndef RUNTIME_H
#define RUNTIME_H

#include <stdbool.h>
#include "libraries.h"

/**
 * Represents a network endpoint with an address and port.
 */
struct Endpoint {
    /** The network address as a signed char pointer. */
    const signed char* address;

    /** The network port number. */
    int port;
};

/**
 * An utility macro to obtain the address of a variable.
 */
#define address(x) _Generic((x),          \
    char*: (const signed char*)(x),       \
    const char*: (const signed char*)(x), \
    default: (x)                          \
)

/** 
 * A mapping from device ids to endpoints representing the neighborhood.
 */
MAP_OF(Neighborhood, DeviceId, struct Endpoint*)

/**
 * Entry point for Scafi3 aggregate programs in a distributed environment with socket-based networking.
 * @param local_id the unique identifier of the device
 * @param port the network port on which the device listens for incoming messages
 * @param neighbors a map of neighboring device IDs to their network endpoints
 * @param aggregate_program the aggregate program to run on the device
 * @param on_result a callback to handle the result of the program execution returning a boolean indicating whether to 
 *                  continue or stop
 */
void engine(
    const BinaryCodable* local_id,
    int port, 
    const Neighborhood neighbors,
    const void* (*aggregate_program)(const AggregateLibrary* library),
    bool (*on_result)(const void* result)
);

#endif // RUNTIME_H
