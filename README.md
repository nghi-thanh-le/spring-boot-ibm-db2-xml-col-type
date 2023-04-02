# Spring Boot Application & IBM DB2

This is an sample of SpringBoot with IBM DB2 database. 

The application is developed on JAVA, while SpringBoot is used as web development framework.
Maven is used as a build tool.

## System Requirements

Java SE | Maven | IBM DB2 | Docker
---------|---------|---------|---------
11 | 3.6.3 | Community Edition (11.5+) | 20.10.17+

## Installation Guide
Installations need to be done before cloning the project.

- The project will run on JAVA and for this you should have Java Development Kit ([JDK](https://jdk.java.net/17/)) that works as a compiler.
- While installing JDK make sure that Java version should be 11 (either OracleJDK or OpenJDK).
- Install [Maven](https://maven.apache.org/users/index.html) on your system.
- Some IDE for example (VS Code, Intellij, NetBeans etc.)

### Database (Optional)
- Install Docker following the instruction for your current OS at [Docker Documentation](https://docs.docker.com/get-docker/).
- Install IBM DB2 Community Edition following the [Db2 Community Edition for Docker](https://www.ibm.com/docs/en/db2/11.5?topic=docker-downloading-installing-editions).

It is optional to install the database, the application can run normally without the database connection. However, the project's APIs will not be able to provide data properly.

### Running the project for developers

- Clone the project from repository.
- If it is the case that developer runs the application locally, then run the mvn command: `mvn spring-boot:run`.
- The API documentation is ready at http://localhost:7070/swagger-ui/index.html

### Running the project with custom application.properties for hosting the application in remote servers
When it comes to running the Spring Boot application in remote servers, it is not recommended to have 
the PROD or STAGE application.properties file in the source code as it is 
possible to trace the git history to find the PROD/STAGE passwords being used for DB connection or JWT token generation.

Hence, the following application.properties template are provided to create a custom file.
```properties
# ===============================
# = General Property
# ===============================
server.port=7070
server.error.include-message=always
server.error.include-stacktrace=never
springdoc.api-docs.path=/api-docs

api-docs.server.localhost=http://localhost:${server.port}
api-docs.server.stage=
api-docs.server.prod=

# ===============================
# = Database connection
# = List of properties: https://www.ibm.com/docs/en/db2/11.5?topic=SSEPGG_11.5.0/com.ibm.db2.luw.apdv.java.doc/src/tpc/imjcc_rjvdsprp.html
# = JDBC example: jdbc:db2://localhost:50000/testdb:progressiveStreaming=2;
# ===============================
spring.datasource.url=jdbc:db2://host:port/database_name:progressiveStreaming=2;optional_connection_properties;
spring.datasource.username=admin
spring.datasource.password=admin

# ===============================
# = Mandatory, should not be changed unless switch to Oracle DB or MSSQL
# ===============================
spring.datasource.driver-class-name=com.ibm.db2.jcc.DB2Driver
# End of DB mandatory
spring.datasource.hikari.max-lifetime=4000
spring.datasource.hikari.connection-timeout=4000
spring.datasource.hikari.validation-timeout=4000
spring.datasource.hikari.maximum-pool-size=40
# Keep the connection alive if idle for a long time (needed in production)
spring.datasource.testWhileIdle=true
spring.datasource.validationQuery=SELECT 1

# ===============================
# = Enable it if this is the first time connection to DB
# = And you would like to have some data in DB
# = classpath normally references to the /src/main/resources folder
# ===============================
#spring.sql.init.mode=always
#spring.sql.init.data-locations=classpath:data.sql

# ===============================
# = Configure size of files in the POST/PUT request.
# ===============================
spring.servlet.multipart.max-file-size=1GB
spring.servlet.multipart.max-request-size=1GB

# ===============================
# = JPA / HIBERNATE
# = Show or not log for each sql query
# = Enable those two properties if you want to check in SQL commands in the log files
# ===============================
#spring.jpa.show-sql=true
#spring.jpa.properties.hibernate.format_sql=true

# ===============================
# = Enable those two properties if you want to check values in SQL commands in the log files
# ===============================
#logging.level.org.hibernate.SQL=debug
#logging.level.org.hibernate.type.descriptor.sql=trace

# ===============================
# = Hibernate ddl auto (create, create-drop, update): with "create-drop" the database
# = schema will be automatically created afresh for every start of application
# ===============================
spring.jpa.generate-ddl=false
spring.jpa.hibernate.ddl-auto=none

# ===============================
# = Naming strategy - Mandatory.
# = Documentation: https://dev.to/aleksey/hibernate-naming-strategies-jpa-specification-vs-spring-boot-opinionation-m1c
# ===============================
spring.jpa.hibernate.naming.implicit-strategy=org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
# ===============================
# = Allows To Hibernate to generate SQL optimized for a particular DBMS
# = those two properties are mandatory.
# ===============================
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.DB2Dialect
spring.jpa.properties.hibernate.hibernate.globally_quoted_identifiers=true

# ===============================
# = JWT
# ===============================
rdtuni.app.jwtSecret=theSecretKey
rdtuni.app.jwtExpirationMs=3600000
rdtuni.app.jwtRefreshExpirationMs=86400000

# ===============================
# = Logger Configuration
# = Documentation: https://docs.spring.io/spring-boot/docs/2.1.18.RELEASE/reference/html/boot-features-logging.html
# ===============================
logging.file.path=./logs/
logging.file.name=${logging.file.path}error.log
#logging.level.root=warn
#logging.level.org.springframework.web=debug
#logging.level.org.hibernate=error
```

From the above `application.properties` template, devs can create their own version and store the file at 
a specific location. Based on this scenario, it is then possible to run the application with the specific properties file instead of `src/main/resources/application.properties`.

- If it is the case that developers run the project in remote machine, then run the mvn command: `mvn spring-boot:start -Dspring-boot.run.arguments=--spring.config.location=<path-to-file>`.
- If it is the case that developers run the project in local machine, then run the mvn command: `mvn spring-boot:run -Dspring-boot.run.arguments=--spring.config.location=<path-to-file>`.
- To stop the SpringBoot application run by the above command, then run the following command: `mvn spring-boot:stop`.

## LOG files
By default, log files based on the definition in the `application.properties` will be created at `project_directory/logs/error.log`. Log files rotate when they reach 10 MB and, as with console output, ERROR -level, WARN -level, and INFO -level messages are logged by default.