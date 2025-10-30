#ifndef LIBRARIES_H
#define LIBRARIES_H

#include <stdbool.h>
#include "field.h"

typedef struct ReturnSending {
    const Field* returning;
    const Field* sending;
} ReturnSending;

static inline ReturnSending* retsend(const Field* val) {
    _Thread_local static ReturnSending rs;
    rs.returning = val;
    rs.sending = val;
    return &rs;
}

static inline ReturnSending* return_sending(const Field* r, const Field* s) {
    _Thread_local static ReturnSending rs;
    rs.returning = r;
    rs.sending = s;
    return &rs;
}

typedef struct AggregateLibrary {
    FieldBasedSharedData Field;
    const BinaryCodable* (*local_id)(void);
    const void* (*branch)(bool condition, const void* (*true_branch)(void), const void* (*false_branch)(void));
    const Field* (*exchange)(const Field* initial, ReturnSending* (*f)(const Field* in));
    const BinaryCodable* (*share)(const BinaryCodable* initial, const BinaryCodable* (*f)(const Field* nvalues));
} AggregateLibrary;

#endif // LIBRARIES_H
