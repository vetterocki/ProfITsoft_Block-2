# Block 2

![Static Badge](https://img.shields.io/badge/Java-17-brown)
![Static Badge](https://img.shields.io/badge/Spring_Framework-6-green)
![Static Badge](https://img.shields.io/badge/Spring_Boot-3.1-green)
![Static Badge](https://img.shields.io/badge/PostgreSQL-14-blue)


## Requirements

- Java 17
- Maven 3.12
- PostgreSQL 8+

  **or**
- Java 17
- Maven 3.12
- Docker Engine	19.03.0+ (for running Postgres container)

## Prerequisites

Before running API execute following command to generate MapStruct classes:
```
mvn spring-boot:run
```

if you have Docker installed, you can run Postgres instance via docker-compose.yaml file:

```
docker compose up -d
```

To run application integration tests, following command must be executed:

```
mvn surefire:test
```

## Additional info

Cars domain structure can be found in `cars.json` file (src/main/resources folder).
Furthermore, this file can be used for uploading endpoint.