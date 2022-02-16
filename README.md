# sports-stream
An app to create and join free sports event in your local area.

## Setup a local environment
A database is required in order for this app to run properly.    
The application is configured to connect to any database which connection details are defined in the following environment variables:
```
DB_URL
DB_USERNAME
DB_PASSWORD
```
See ``config/JpaConfig `` for implementation details and customisation.    

You may need to define additional properties to ensure that Spring creates the necessary tables and schema if none exist and specify a dialect.   
See below, an example of a possible configuration using PostgreSQL.
````properties
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL92Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
spring.jpa.hibernate.naming.implicit-strategy=org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl
````
