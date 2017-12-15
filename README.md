# Project Title

Webcrawler is a SpringBoot application which provides a REST API to access services to recursively scan web pages and its links to other web pages.

## Features

SpringBoot,
REST API,
Security,
JUnit,
JSoup

## Quick description

Instead of working off the loading of the web pages sequentially i created multiple threads to load the linked web pages in parallel. 
This should increase the throughput dramatically. The threads are managed within a Countdown Latch construct.


## What else could have been done

For the assessment I wanted to limit the scope of this application and therefore only a limited set of functions was 
implemented. Only a few specific error handlers are to find in the code base as well as the code coverage of JUnit tests is not complete.

Other useful addons i could think of would be:
- limit of maximum amount of simultaneous web requests
- retries if downloads of web pages are failing or running into a timeouts
- Micro service support e.g. service discovery etc.

## Getting Started

Build project

**`TODO:`** Start Springboot application

**`TODO:`** Sample web request

Credentials:
webcrawler/wbpwd


### Prerequisites

JDK 8
Maven 3
Free ports on local machine: 8080, 1080

### Installing

**`TODO:`**
MAVEN COMMAND...

## Running the tests

Running the test will start a mock server listening on port 1080

**`TODO:`**
MAVEN COMMAND...

### And coding style tests

**`TODO:`**
Git HOOKS? Explain what these tests test and why

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Anton Schertenleib** - *Initial work* - [Webcrawler](https://github.com/scherti/webcrawler)


## Acknowledgments

Thanks to my Girlfriend for tolerating me ruining our Friday night by quickly hacking this! :-)