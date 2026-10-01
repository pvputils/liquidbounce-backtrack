@echo off
setlocal
set "APP_HOME=%~dp0"
set "JAVA_EXE=java.exe"
if defined JAVA_HOME set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
if not exist "%APP_HOME%.gradle\bootstrapTodoAi" mkdir "%APP_HOME%.gradle\bootstrapTodoAi"
> "%APP_HOME%.gradle\bootstrapTodoAi\settings.gradle" echo include ':backtrack'; project(':backtrack').projectDir = file('../..'); project(':backtrack').buildFileName = 'buildTodoAi.gradle'
"%JAVA_EXE%" -classpath "%APP_HOME%gradle\wrapper\gradle-wrapperTodoAi.jar" org.gradle.wrapper.GradleWrapperMain -p "%APP_HOME%.gradle\bootstrapTodoAi" %*
exit /b %ERRORLEVEL%
