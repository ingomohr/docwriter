![Build](https://github.com/ingomohr/docwriter/actions/workflows/mvn-build-main.yml/badge.svg?branch=master)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
### What is this?
DocWriter is a Java API to simplify writing docx documents...

- (optionally) based on template files
- with option to replace text - also by specification through regex - on the document
- with option to append content as markdown to the document
- with option to insert and to update a table-of-contents (ToC)

### Examples
See [Examples](https://github.com/ingomohr/docwriter/wiki/Examples)

### Requirements
- Java 8


### How to Build
:bulb: This will also build the JAR with all the dependencies included.

Call
```
mvn package
```

### Third Party Components Used
This component uses the following further components
* [docx4j 8.3.8](https://github.com/plutext/docx4j), Apache 2.0
* [flexmark 0.50.50](https://github.com/vsch/flexmark-java) BSD 2-Clause "Simplified" License
