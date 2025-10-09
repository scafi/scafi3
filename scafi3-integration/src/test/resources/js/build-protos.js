/* This script generates JavaScript code from all proto files in the current directory using protobufjs. */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const protoFiles = fs.readdirSync('.').filter(file => file.endsWith('.proto'));

if (protoFiles.length === 0) {
    process.exit(0);
}

protoFiles.forEach(protoFile => {
    const outputFile = protoFile.replace('.proto', '.js');
    const command = `npx pbjs -t static-module -w commonjs -o ${outputFile} ${protoFile}`;
    execSync(command, { stdio: 'inherit' });
});
