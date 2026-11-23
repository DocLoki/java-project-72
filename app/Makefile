
.PHONY: setup build build-css start render-start

setup: build-css
	gradlew.bat installDist

build: setup

build-css:
	npm ci
	npm run build:css

start:
	npx concurrently "npm run watch:css" "cmd /c gradlew.bat run"

render-start:
	./build/install/app/bin/app
