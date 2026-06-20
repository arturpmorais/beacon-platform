.PHONY: dev stop test build logs

dev:
	./scripts/dev.sh

stop:
	./scripts/stop.sh

test:
	./scripts/test.sh

build:
	./scripts/build.sh

logs:
	./scripts/logs.sh
