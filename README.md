Jenjin
=====

The Jenjin is a flexible server architecture designed for use in MMORPGs.
Programmed in Java, and built with Gradle, it is runnable on virtually any Operating
system.

The project is made up of several modules; the modules including the name "world" contain
basic MMORPG functionality including persistence, synchronized action and movement, and
line-of-sight visibility.  It also includes a format for XML files that can be used to
pre-initialize the game world.  These modules are not required for the Jenjin functionality
and can be foregone completely if desired.

Modules without "world" in the name are part of the core Jenjin; they are necessary for the
core threading and networking functionality of the Jenjin.


***

##Building and Testing

The Jenjin is built using Gradle, and can be built and tested with a simple command on
any operating system running Java 7; simply run

`./gradlew build`


***

##Dependencies

The Jenjin core architecture uses the following unmodified libraries:

* **Testing**
    * The Jenjin uses [TestNG](http://testng.org/doc/index.html) for unit tests. ([License](http://testng.org/license/))
    * For database integration tests, [H2Database](http://h2database.com/html/main.html) is used. ([License](http://h2database.com/html/license.html))