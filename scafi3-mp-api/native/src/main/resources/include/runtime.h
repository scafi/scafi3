#ifndef RUNTIME_H
#define RUNTIME_H

#include <stdbool.h>
#include "libraries.h"

typedef struct ConnectionOrientedNetworkManagerImpl* ConnectionOrientedNetworkManager;

struct Endpoint {
    char* address;
    int port;
};

MAP_OF(Neighborhood, DeviceId, struct Endpoint*)

ConnectionOrientedNetworkManager socket_network(
    const BinaryCodable* device_id,
    int port, 
    const Neighborhood neighbors
);

void engine(
    const ConnectionOrientedNetworkManager network_manager,
    const void* (*aggregate_program)(const AggregateLibrary* library),
    bool (*on_result)(const void* result)
);

#endif // RUNTIME_H
