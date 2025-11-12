#ifndef UTILS_H
#define UTILS_H

/******************************************************************************************************************
 * Control structures
 *****************************************************************************************************************/
#include "internals.h"

/**
 * Utility macro to define lambda functions.
 * @param ret Return type of the function.
 * @param args Arguments of the function.
 * @param body Body of the function.
 * 
 * Example usage:
 * ```c
 * lambda(int, (int a, int b), {
 *     return a + b;
 * });
 */
#define lambda(ret, args, body) \
    ({ \
    ret __fn__ args body \
    &__fn__; \
    })

/**
 * Shorthand macro for defining lambda functions. See `lambda` for details.
 * @param args Arguments of the function.
 * @param body Body of the function.
 * 
 * Example usage:
 * ```c
 * fn((int a, int b), {
 *    return a + b;
 * });
 * ```
 */
#define fn lambda

#define EQ_CAST(key) _Generic((key),        \
    const Eq*: (const Eq*)(key),            \
    Eq*: (const Eq*)(key),                  \
    const BinaryCodable*: (const Eq*)(key), \
    BinaryCodable*: (const Eq*)(key))

/**
 * Defines a map type with specified key and value types.
 */
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
    static inline void Name##_foreach(const Name map, void (*f)(const ValuesTypePtr value)) {            \
        map_foreach(map, (void(*)(const void*))f);                                                       \
    }                                                                                                    \
    static inline size_t Name##_size(const Name map) {                                                   \
        return map_size(map);                                                                            \
    }                                                                                                    \
    static inline void Name##_free(Name map) {                                                           \
        map_free(map);                                                                                   \
    }

/**
 * A generic array structure.
 */
typedef struct Array {
    /** Array items. */
    void** items;

    /** Size of the array. */
    size_t size;
} Array;

#endif // UTILS_H
