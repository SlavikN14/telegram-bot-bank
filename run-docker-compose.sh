#!/bin/bash

./gradlew clean

./gradlew assemble

docker compose up --force-recreate --build -d
