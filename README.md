# Capacitor Zip

[![npm](https://img.shields.io/npm/v/capacitor-zip.svg)](https://www.npmjs.com/package/capacitor-zip)
[![npm](https://img.shields.io/npm/dt/capacitor-zip.svg?label=npm%20downloads)](https://www.npmjs.com/package/capacitor-zip)
[![Build Status](https://travis-ci.org/triniwiz/capacitor-zip.svg?branch=master)](https://travis-ci.org/triniwiz/capacitor-zip)

## Installation

- `npm i capacitor-zip`

## Usage

```ts
import { Zip } from 'capacitor-zip';
const zip = new Zip();

await zip.zip({
    source : source,
    destination: destination,
    keepParent: true, // Optional default true
    password: 'password', // Optional
},(progress)=>{});

await zip.unZip({
    source : source,
    destination: destination,
    overwrite: true, // Optional default true
    password: 'password', // Optional
},(progress)=>{});

