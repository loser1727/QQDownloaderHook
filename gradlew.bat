@if "%~1" == "" goto default
@if "%~1" == "debug" goto debug
@if "%~1" == "debug-nightly" goto debug_nightly

:default
@rem Default Gradle execution
java -jar "%~dp0gradle\wrapper\gradle-wrapper.jar" %*
@goto end

:debug
@rem Debug mode - print Gradle execution details
java -jar "%~dp0gradle\wrapper\gradle-wrapper.jar" %* --debug
@goto end

:debug_nightly
@rem Debug nightly build
java -jar "%~dp0gradle\wrapper\gradle-wrapper.jar" %* --debug --daemon
@goto end

:end
