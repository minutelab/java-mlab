# Integrate MinuteLab labs into java "Unit" tests

[![Build Status](https://travis-ci.org/minutelab/java-mlab.svg?branch=develop)](https://travis-ci.org/minutelab/java-mlab)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Support for integrating [MinuteLab](http://minutelab.io) labs inside Java "unit" testing.

Write "unit" tests that can set up servers on demand.

With MinuteLab you can decorate JUnit tests with rules that make sure that you have the appropriate
environment set up for the test. For example you can treat a real postgres database as an embedded database.
