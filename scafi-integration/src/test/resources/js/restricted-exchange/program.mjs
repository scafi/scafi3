function aggregateProgram(lang) {
    return lang.branch(
        lang.localId % 2 == 0,
        () => lang.exchange(lang.Field.of(true), n => returnSending(n)),
        () => lang.exchange(lang.Field.of(false), n => returnSending(n))
    );
}
