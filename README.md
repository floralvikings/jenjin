Jenjin ![Build Status](https://travis-ci.org/floralvikings/jenjin.svg?branch=jenjin-184)
=====

The Jenjin is a flexible server architecture designed for use in MMORPGs.
Programmed in Java, and built with Gradle, it is runnable on virtually any operating
system; it has been tested in OSX 10.8 and 10.9, Ubuntu 14.04, Windows 7, and is integrated
with Travis CI for build automation.

The project is made up of several modules; the modules including the name "world" contain
basic MMORPG functionality including authentication, basic persistence, and synchronized movement.
It also includes a format for XML files that can be used to pre-initialize the game world.  
These modules are not required for the core Jenjin functionality and can be foregone completely if desired.

Modules without "world" in the name are part of the core Jenjin; they are necessary for the
core threading and networking functionality of the Jenjin.  They include the XML Message registry and ExecutableMessage
system that allows for easy extension of client and server responses to messages, as well as thread management
allowing a server to support a large number of clients.

***

##Requirements

Building and testing the Jenjin requires Java 8.


***

##Building and Testing

To build and test the Jenjin, run

`./gradlew build`


***

##Special Thanks

Special thanks go out to:

* This blog [http://seamless-pixels.blogspot.co.uk/], which supplied all the textures used in the demo application.

***

##Dependencies

The Jenjin itself does not use any third party dependencies; however, the tests do utilize the following:

* The Jenjin uses [TestNG](http://testng.org/doc/index.html) for unit tests. ([License](http://testng.org/license/))
* For database integration tests, [H2Database](http://h2database.com/html/main.html) is used. ([License](http://h2database.com/html/license.html))
