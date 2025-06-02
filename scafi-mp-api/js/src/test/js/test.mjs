'use strict';

import { ScafiApi } from "../../../target/scala-3.6.4/scafi-mp-api-fastopt/main.mjs";

function aggregateProgram(language) {
    return language.branch(true, () => 10, () => 20);
}

const deviceId = 1234;

const result = ScafiApi.xcEngine(deviceId, s => aggregateProgram(s));
console.log("Result: ", result);
