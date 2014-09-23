Java Spring Web App Starter Project
=============================

[![Build Status](https://travis-ci.org/jtuchscherer/java-starter.png)](https://travis-ci.org/jtuchscherer/java-starter)


Instructions
------------

Build this project with

    mvn clean package

Run integration tests with

    mvn clean verify

You can run it with

    mvn clean tomcat7:run

Then you can test the api with curl like this

    curl -i -H "Accept: application/json" -H "Content-Type: application/json" -XPOST localhost:8080/rest/users -d '{"name": "tom", "email": "test@admin.com", "roles": [{"name": "dev"}]}'

    curl -i -H "Accept: application/json" localhost:8080/rest/users

Notes
------------

* Uses Jersey 2.12 for REST server
* Spring 3.2.6.RELEASE (web.xml got replaced by WebAppInitializer)
* This app uses an embedded HSQL database to store the users and Hibernate as an ORM
* Logging is provided by logback
* There are end-to-end integration tests that start an embedded tomcat server and use HttpClient to query the API
