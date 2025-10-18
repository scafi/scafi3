#ifndef UTILS_H
#define UTILS_H

/******************************************************************************************************************
 * Control structures
 *****************************************************************************************************************/
#include "internals.h"

#define lambda(ret, args, body) \
    ({ \
    ret __fn__ args body \
    &__fn__; \
    })

#define fn lambda

#define MAP_OF(Name, KeysTypePtr, ValuesTypePtr)                                                \
    typedef Map Name;                                                                           \
    static inline Name Name##_empty() {                                                         \
        return map_empty();                                                                     \
    }                                                                                           \
    static inline void Name##_put(Name map, const KeysTypePtr key, const ValuesTypePtr value) { \
        map_put(map, key, value);                                                               \
    }                                                                                           \
    static inline ValuesTypePtr Name##_get(const Name map, const KeysTypePtr key) {             \
        return (ValuesTypePtr)map_get(map, key);                                                \
    }                                                                                           \
    static inline size_t Name##_size(const Name map) {                                          \
        return map_size(map);                                                                   \
    }                                                                                           \
    static inline void Name##_free(Name map) {                                                  \
        map_free(map);                                                                          \
    }

#endif // UTILS_H
