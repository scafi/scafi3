function aggregateProgram(lang) {
    return lang.branch(
        lang.localId % 2 === 0,
        () => lang.exchange(lang.Field.of(1), n => returnSending(n)),
        () => lang.exchange(lang.Field.of(0), n => returnSending(n))
    );
}
