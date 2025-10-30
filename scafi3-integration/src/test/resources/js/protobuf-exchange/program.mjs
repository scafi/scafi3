import proto from "./messages.js";

function aggregateProgram(lang) {
    const temperature = senseTemperature();
    return lang.exchange(lang.Field.of(temperature), n =>
        returning(n).send(lang.Field.of(temperature))
    );
}

function senseTemperature() {
    return proto.TemperatureSensor.create({ id: `#${deviceId}`, temperature: deviceId * 10 });
}

proto.TemperatureSensor.prototype.toString = function () {
    return `Sensor{id=${this.id}, temp=${this.temperature.toFixed(2)}}`;
};
