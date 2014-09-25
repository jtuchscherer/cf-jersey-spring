Java Spring Web App Starter Project
=============================

[![Build Status](https://travis-ci.org/jtuchscherer/cf-jersey-spring.svg?branch=master)](https://travis-ci.org/jtuchscherer/cf-jersey-spring)


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
    
You can push it to CloudFoundry with the following commands

For bosh-lite

    mvn clean cf:push -Pbosh-lite (this assumes you have a local bosh-lite install with an org 'Java' and a space 'dev')

For PWS
    
    mvn clean cf:push -PPWS (you will need to modify the org and space in the pom.xml)
    
In order to get these two commands to work, you will need this in your ~/.m2/settings.xml

````
<settings>
  [...]
  <servers>
    [...]
    <server>
      <id>bosh-lite</id>
      <username>admin</username>
      <password>admin</password>
    </server>
    <server>
      <id>pws</id>
      <username>PWS_USERNAME</username>
      <password>PWS_PASSWORD</password>
    </server>
  </servers>
</settings>
````

Notes
------------

* Uses Jersey 2.12 for REST server
* Spring 3.2.11.RELEASE (web.xml got replaced by WebAppInitializer)
* This app uses an embedded HSQL database to store the users and Hibernate as an ORM
* Logging is provided by logback
* There are end-to-end integration tests that start an embedded tomcat server and use HttpClient to query the API
