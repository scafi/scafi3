const void* aggregate_program(const AggregateLibrary* lang) {
    const void* return_exchanging_field(const Field* field) {
        return lang->exchange(field, fn(ReturnSending, (const Field* nvalues), {
            return return_sending(nvalues, field);
        }));
    }

    const void* is_even_branch() {
        return return_exchanging_field(lang->Field.of(int_of(1)));
    }

    const void* is_odd_branch() {
        return return_exchanging_field(lang->Field.of(int_of(0)));
    }

    return lang->branch({{ deviceId }} % 2 == 0, is_even_branch, is_odd_branch);
}
