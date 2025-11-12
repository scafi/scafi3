const void* aggregate_program(const AggregateLibrary* lang) {
    return lang->neighbor_values(lang->local_id());
}
