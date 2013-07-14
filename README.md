Jenjin
=====

The Jenjin is a powerful, flexible server architecture designed for use in MMORPGs.
Programmed in Java, and built with Gradle, it is runnable on virtually any Operating
system.

The project is made up of a Server and Client framework, code-named the Jenjin
Game Server Framework and Jenjin Game Client Framework, respectively.

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
| ------------- | ----------------------------------------------------------------- | ------------------------------------------------ | ---------- |
| TestAccount01 | 8b678bbcf5cf2a60c6dc631b01d6b3c77d142d05eb521a62f73014cc987e0156  | 66db065da6853ec1dafb45933c77b3fdac9ce354a391e8d3 | 0          |
| TestAccount02 | 650f00f552d4df0147d236e240ccfc490444f4b358c4ff1d79f5fd90f57243bd  | e3c42b85a183d3f654a3d2bb3bc5ea607d0fb529d9b890d3 | 0          |

Any tests that utilize the login functionality will fail without this table.

The password and salt fields are necessary for the salted SAH256 hashing that is done to user passwords.

## Known Issues
The current implementation of the JGSF and JGCF do not implement any password encryption; although passwords are expected
to be hashed and salted in the database, the password is sent from the client to the server over an unencrypted socket.

Ther is currently no fix planned for this, as the login functionality currently implemented by the Jenjin is not intended
for production, but simply an example.


##Dependencies

The Jenjin core architecture uses the following unmodified libraries:

* [JUnit](https://github.com/junit-team/junit)
    * License: EPL (Commercial Friendly)
* [Hamcrest](https://github.com/hamcrest/JavaHamcrest)
    * License: BSD (Commercial Friendly)
* [Drizzle](https://github.com/krummas/DrizzleJDBC)
    * License: BSD (Commercial Friendly)
