#ifndef LIBRARIES_H
#define LIBRARIES_H

#include <stdbool.h>
#include "field.h"

typedef struct ReturnSendingImpl* ReturnSending;

ReturnSending retsend(const Field* value);

ReturnSending return_sending(const Field* returning, const Field* send);

typedef const void* (*branch_callback_t)(void);

typedef struct AggregateLibrary {
    FieldBasedSharedData Field;
    struct { // Common library
        const BinaryCodable* (*local_id)(void);
        const Field* (*device)(void);
    };
    struct { // Branching library
        const void* (*branch)(bool condition, branch_callback_t true_branch, branch_callback_t false_branch);
    };
    struct { // Exchange library
        const Field* (*exchange)(const Field* initial, ReturnSending (*f)(const Field* in));
    };
} AggregateLibrary;

#endif // LIBRARIES_H
