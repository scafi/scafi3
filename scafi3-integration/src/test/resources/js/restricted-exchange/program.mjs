function aggregateProgram(lang) {
    return lang.branch(
        lang.localId % 2 === 0,
        () => lang.neighborValues(1),
        () => lang.neighborValues(0),
    );
}
