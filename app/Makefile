
.PHONY: setup build build-css start render-start lint

ifeq ($(OS),Windows_NT)
GRADLEW = gradlew.bat
GRADLE_RUN = cmd /c gradlew.bat run
else
GRADLEW = ./gradlew
GRADLE_RUN = APP_ENV=development ./gradlew run
endif

setup: build-css
	$(GRADLEW) installDist

build: setup

build-css:
	npm ci
	npm run build:css

start:
	npx concurrently "npm run watch:css" "$(GRADLE_RUN)"

render-start:
	./build/install/app/bin/app

lint:
	$(GRADLEW) checkstyleMain checkstyleTest
