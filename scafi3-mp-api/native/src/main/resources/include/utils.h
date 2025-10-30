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

#define EQ_CAST(key) _Generic((key),        \
    const Eq*: (const Eq*)(key),            \
    Eq*: (const Eq*)(key),                  \
    const BinaryCodable*: (const Eq*)(key), \
    BinaryCodable*: (const Eq*)(key))

#define MAP_OF(Name, KeysTypePtr, ValuesTypePtr)                                                         \
    typedef Map Name;                                                                                    \
    static inline Name Name##_empty() {                                                                  \
        return map_empty();                                                                              \
    }                                                                                                    \
    static inline ValuesTypePtr Name##_put(Name map, const KeysTypePtr key, const ValuesTypePtr value) { \
        return (ValuesTypePtr)map_put(map, EQ_CAST(key), value);                                         \
    }                                                                                                    \
    static inline ValuesTypePtr Name##_get(const Name map, const KeysTypePtr key) {                      \
        return (ValuesTypePtr)map_get(map, EQ_CAST(key));                                                \
    }                                                                                                    \
    static inline size_t Name##_size(const Name map) {                                                   \
        return map_size(map);                                                                            \
    }                                                                                                    \
    static inline void Name##_free(Name map) {                                                           \
        map_free(map);                                                                                   \
    }

#endif // UTILS_H
