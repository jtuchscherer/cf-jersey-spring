Java Spring Web App Starter Project
=============================

Instructions
------------

Build this project with

    mvn package

Run integration tests with

    mvn integration-test

You can run it with

    mvn tomcat7:run

Then you can test the api by going to

    http://localhost:8080/java-refactoring-test/rest/users

Notes
------------

* Uses Jersey for REST server
* Spring 3.2.6.RELEASE (web.xml got replaced by WebAppInitializer)
* This app uses an embedded HSQL database to store the users and Hibernate as an ORM
* Logging is provided by logback
* There are end-to-end integration tests that start an embedded tomcat server and use HttpClient to query the API