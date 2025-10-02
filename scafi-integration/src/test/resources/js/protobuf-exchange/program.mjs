import proto from "./messages.js";

function aggregateProgram(lang) {
    const temperature = senseTemperature();
    return lang.exchange(lang.Field.of(temperature), n =>
        returning(n).send(lang.Field.of(temperature))
    );
}

proto.TemperatureSensor.prototype.toString = function () {
    return `Sensor(id=${this.id}, temp=${this.temperature})`;
};

function senseTemperature() {
    return proto.TemperatureSensor.create({ id: `#${deviceId}`, temperature: deviceId * 10 });
}
