JAVA_HOME := /opt/java/jdk17
GRADLE_HOME := /opt/gradle/gradle-7.3.3

RELEASE_VERSION := $(shell grep ^version= lib/gradle.properties | sed -e "s/version=//")

setup:

test:
	$(GRADLE_HOME)/bin/gradle test

build: test
	$(GRADLE_HOME)/bin/gradle jar

publish:
	$(GRADLE_HOME)/bin/gradle clean publish
version:
	git add -u || echo "OK"
	git commit -m "Version $(RELEASE_VERSION)" || echo "same version"
	git push || echo "OK"

.PHONY: build
