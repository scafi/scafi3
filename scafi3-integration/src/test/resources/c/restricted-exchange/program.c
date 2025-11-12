const void* aggregate_program(const AggregateLibrary* lang) {
    const void* is_even_branch() {
        return lang->neighbor_values(int_of(1));
    }

    const void* is_odd_branch() {
        return lang->neighbor_values(int_of(0));
    }

    return lang->branch({{ deviceId }} % 2 == 0, is_even_branch, is_odd_branch);
}
