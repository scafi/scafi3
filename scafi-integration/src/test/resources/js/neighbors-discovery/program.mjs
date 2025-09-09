function aggregateProgram(lang) {
    return lang.exchange(lang.Field.of(lang.localId), n =>
        returning(n).send(lang.Field.of(lang.localId))
    );
}
