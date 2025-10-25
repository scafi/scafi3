#ifndef INTERNALS_H
#define INTERNALS_H

#include <stdlib.h>
#include <stdbool.h>
#include "message.h"

typedef struct MapImpl* Map;

Map map_empty();
void* map_put(Map map, const Eq* key, const void* value);
void* map_get(const Map map, const Eq* key);
size_t map_size(const Map map);
void map_free(Map map);

#endif // INTERNALS_H
