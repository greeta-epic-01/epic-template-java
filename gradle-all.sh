#! /bin/bash -e

TASKS="$*"

if [ -z "$TASKS" ] ; then
  TASKS=build
fi

cd service-chassis

./gradlew $TASKS
./gradlew publish

cd ../service-template
./gradlew $TASKS
