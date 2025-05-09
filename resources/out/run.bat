@echo off
pushd "%~dp0"
java -cp "build.jar;../../src/main/resources/karina_base.jar" main
popd
@pause