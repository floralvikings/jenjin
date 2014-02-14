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

The Jenjin is built using Gradle, and can be built with a simple command on
any operating system that supports Java 7; simply run

`./gradlew build`

In order for the tests to run properly, you must be running a MySQL server on localhost on port 3306,
with a database named "jenjin_test", a user with permission to SELECT and UPDATE named "jenjin_user" with
a password "jenjin_password"

This database must contain a table called "users" laid out in the following way:

**users**


| username               | password                                                          | salt                                             | loggedin   | xCoord | yCoord | zoneID |
| ---------------------- | ----------------------------------------------------------------- | ------------------------------------------------ | ---------- | ------ | ------ | ------ |
| TestAccount1           | 8b678bbcf5cf2a60c6dc631b01d6b3c77d142d05eb521a62f73014cc987e0156  | 66db065da6853ec1dafb45933c77b3fdac9ce354a391e8d3 | 0          | 0      | 0      | 0      |
| TestAccount2           | 650f00f552d4df0147d236e240ccfc490444f4b358c4ff1d79f5fd90f57243bd  | e3c42b85a183d3f654a3d2bb3bc5ea607d0fb529d9b890d3 | 0          | 0      | 0      | 0      |
| TestAccount...(to 99)  | 650f00f552d4df0147d236e240ccfc490444f4b358c4ff1d79f5fd90f57243bd  | e3c42b85a183d3f654a3d2bb3bc5ea607d0fb529d9b890d3 | 0          | 0      | 0      | 0      |

Any tests that utilize the login functionality will fail without this table.

The password and salt fields are necessary for the salted SAH256 hashing that is done to user passwords.

If testing is interrupted before completion, it may be necessary to manually reset the loggedin and/or xCoord and yCoord
fields in the MySQL database.



##Dependencies

The Jenjin core architecture uses the following unmodified libraries:

* [TestNG](http://testng.org/doc/index.html)
    * License: Apache 2.0
* [Hamcrest](https://github.com/hamcrest/JavaHamcrest)
    * License: BSD
* [Drizzle](https://github.com/krummas/DrizzleJDBC)
    * License: BSD
