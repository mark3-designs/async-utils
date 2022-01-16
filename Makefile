JAVA_HOME := /opt/java/jdk17
GRADLE_HOME := /opt/gradle/gradle-7.3.3
#JAVA_HOME := /opt/java/jdk1.8.0_231
#GRADLE_HOME := /opt/gradle/gradle-6.5.1
PATH := $(JAVA_HOME)/bin:$(GRADLE_HOME)/bin:$(PATH)
GRADLE_TASKS = clean jar debian 

RELEASE_VERSION := $(shell grep ^version= lib/gradle.properties | sed -e "s/version=//")

$(GRADLE_TASKS):
	$(GRADLE_HOME)/bin/gradle $@ --info

test:
	$(GRADLE_HOME)/bin/gradle test

build: test
	cd async-utils-j8 && make build
	$(GRADLE_HOME)/bin/gradle jar

publish:
	cd async-utils-j8 && make clean publish
	$(GRADLE_HOME)/bin/gradle clean publish
version:
	git add -u || echo "OK"
	git commit -m "Version $(RELEASE_VERSION)" || echo "same version"
	git push || echo "OK"

.PHONY: build
