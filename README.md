Jenjin
=====

The Jenjin is a powerful, flexible server architecture designed for use in MMORPGs.
Programmed in Java, and built with Gradle, it is runnable on virtually any Operating
system.

The project is made up of a Server and Client framework, code-named the Jenjin
Game Server Architecture and Jenjin Game Client, respectively.

***

##Building and Testing

The Jenjin is built using Gradle, and can be build with a simple command on
any operating system that supports Java; simply run

`gradlew build`

in the root project directory.  Windows users may need to run

`gradlew.bat build`

In order for the tests to run properly, you must be running a MySQL server on localhost on port 3306,
with a database named "jenjinst_chatservertest", a user with permission to SELECT and UPDATE named "jenjinst_cstest" with
a password "chat_test"

This database must contain a table called "users" laid out in the following way:

**users**


| username      | password                                                          | salt                                             | loggedin   |
| ------------- | ------------------------------------------------------------------| -------------------------------------------------|------------|
| TestAccount01 | 910aca6b0695823b46f3f1f7159d649567616cf372bcd53590f6bf447e3d4fbd  | 3bec05e23c362e98a6cc59562fb942fdd7ae683f0f263eee | 0          |
| TestAccount02 | 8ea23cbda53b058a8a49dca4c798d08b3a2e520d6b41c9ad24f08d78ba01317d  | 777993500bd89be2669d9956a93d1d6e3cbe7889598a673f | 0          |

Any tests that utilize the login functionality will fail without this table.


##Dependencies

The Jenjin core architecture uses the following unmodified libraries:

* [JUnit](https://github.com/junit-team/junit)
    * License: EPL (Commercial Friendly)
* [Hamcrest](https://github.com/hamcrest/JavaHamcrest)
    * License: BSD (Commercial Friendly)
* [javassist](https://github.com/jboss-javassist/javassist)
    * License: MPL (Commercial Friendly)
* [reflections](http://code.google.com/p/reflections/)
    * License: WTFPL (Literally anything)
* [Drizzle](https://github.com/krummas/DrizzleJDBC)
    * License: BSD (Commercial Friendly)
* [jsr305](code.google.com/p/jsr-305/)
    * License: BSD (Commercial Friendly)
* [guava](http://code.google.com/p/guava-libraries/)
    * License: Apache (Commercial Friendly)
* [dom4j](http://dom4j.sourceforge.net/)
    * License: BSD (Commercial Friendly)
* [xml-apis](http://www.openoffice.org/external/forms/xml-apis.html)
    * License: Apache (Commercial Friendly)
