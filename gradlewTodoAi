#!/bin/sh
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
JAVA_EXE=java
if [ -n "$JAVA_HOME" ]; then JAVA_EXE="$JAVA_HOME/bin/java"; fi
mkdir -p "$APP_HOME/.gradle/bootstrapTodoAi"
printf "%s\n" "include ':backtrack'; project(':backtrack').projectDir = file('../..'); project(':backtrack').buildFileName = 'buildTodoAi.gradle'" > "$APP_HOME/.gradle/bootstrapTodoAi/settings.gradle"
exec "$JAVA_EXE" -classpath "$APP_HOME/gradle/wrapper/gradle-wrapperTodoAi.jar" org.gradle.wrapper.GradleWrapperMain -p "$APP_HOME/.gradle/bootstrapTodoAi" "$@"
