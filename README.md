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

Building and testing the Jenjin requires Java 8.  The gradle wrapper is built in to the repository; the only thing
you need to do is make sure your ```$JAVA_HOME``` environment variable points to a Java 8 JDK.  All dependencies (there
are very few) will be downloaded automatically by the Gradle wrapper.

***

##Building and Testing

To build and test the Jenjin, run

`./gradlew build`

in the project directory.  This will download the Gradle wrapper to a local folder, and use it to assemble and test the
entire repository.

Be forewarned that there are a few tests that are occasionally... wonky.  If your build fails the first time, please
make sure to try again before creating a ticket, and note in the ticket that the build doesn't fail 100% of the time
if it passes on the second try.  Efforts are currently being made to track down and eliminate all spurious tests, and
the Travis automated building is helping with that a great deal.

***

##Special Thanks

Special thanks go out to:

* This blog [http://seamless-pixels.blogspot.co.uk/], which supplied all the textures used in the demo application.  These
textures are high-resolution, tileable, and very, very nice.  They are completely free but there is an option to download the entire
archive for as little as $1.99 should you desire to support an open-source artist. (You should.)Yet 

***

##Dependencies

The Jenjin itself does not use any third party dependencies; however, the tests do utilize the following:

* The Jenjin uses [TestNG](http://testng.org/doc/index.html) for unit tests. ([License](http://testng.org/license/))
* For database integration tests, [H2Database](http://h2database.com/html/main.html) is used. ([License](http://h2database.com/html/license.html))
