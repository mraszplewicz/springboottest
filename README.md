# springboot test project

Playground project.

Technlogies/frameworks used:
* spring-boot
* spring-mvc rest controllers
* gradle
* hibernate
* querydsl
* liquibase

There are many connection strings, datasources, entity managers. Project is being used to test SQL Server AlwaysOn features.

## Usage

There are two config files where you can find connection strings:
* controllers/src/main/resources/application.properties - 4 application connection strings
* gradle.properties - liquibase_* to generate update and rollback sqls and liquibase_diff_* to generate changelog from entity classes

You can create postgresql database springboottest with user springboottest and password springboottest or change settings

When you have database, you can generate updatesql script using:
```gradle liquibaseUpdateSQL```
It will generate ```liquibase_update.sql``` script in model/build. Run it in your database to create all required objects.

Use ```gradle bootRun``` or ```java -jar tomcatwar.jar``` (in tomcatwar/build/libs after ```gradle build```) or start ```controllers/src/pl.mrasoft.springboottest.Application``` main in your IDE.

Other usefull tasks:
* ```gradle liquibaseFutureRollbackSQL``` generates ```liquibase_rollback.sql``` script in model/build, doesn't work when database is empty
* ```gradle liquibaseDiffChangelog``` generates ```changelog-generated.xml``` script in model/build which contains generated liquibase changelog comparing your liquibase_diff_* database and entity classes. You can modify it and place in model/db/liquibase/changelog
