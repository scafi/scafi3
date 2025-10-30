#ifndef RUNTIME_H
#define RUNTIME_H

#include <stdbool.h>
#include "libraries.h"

struct Endpoint {
    char* address;
    int port;
};

MAP_OF(Neighborhood, DeviceId, struct Endpoint*)

void engine(
    const BinaryCodable* device_id,
    int port, 
    const Neighborhood neighbors,
    const void* (*aggregate_program)(const AggregateLibrary* library),
    bool (*on_result)(const void* result)
);

#endif // RUNTIME_H
