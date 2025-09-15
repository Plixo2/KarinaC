@echo off
pushd "%~dp0"
java -cp "out/build.jar;libs/karina.base.jar" main
popd
