class TemperatureSensor {
    constructor(id, temperature) {
        this.id = id;
        this.temperature = temperature;
    }

    static typeName = "TemperatureSensor";

    static encode(sensor) {
        return JSON.stringify({
            id: sensor.id,
            temperature: sensor.temperature,
        });
    }

    static decode(data) {
        const obj = JSON.parse(data);
        return new TemperatureSensor(obj.id, obj.temperature);
    }

    toString() {
        return `Sensor(id=${this.id}, temp=${this.temperature})`;
    }
}

function aggregateProgram(lang) {
    const temperature = senseTemperature();
    return lang.exchange(lang.Field.of(temperature), n =>
        returning(n).send(lang.Field.of(temperature))
    );
}

function senseTemperature() {
    return new TemperatureSensor(`#${deviceId}`, deviceId * 10);
}
