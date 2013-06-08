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


| username      | password                                 | loggedin   |
| ------------- | -----------------------------------------| -----------|
| TestAccount01 | 82f8809f42d911d1bd5199021d69d15ea91d1fad | 0          |
| TestAccount02 | 82f8809f42d911d1bd5199021d69d15ea91d1fad | 0          |

Any tests that utilize the login functionality will fail without this table.

The database uses a SHA1 hash to secure passwords; this is a known security issue
and a fix is on the way.

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