# Batch Data Migration 🚀

This repository contains a project that demonstrates how to migrate data between two relational databases, MySQL and PostgreSQL, using **Spring Batch**. The goal is to handle large-scale data migrations efficiently while addressing differences in database syntax and data types.

## Table of Contents

- [Introduction](https://www.notion.so/Batch-Data-Migration-206f25d9ba174b9f920b2ef7d0bcc133?pvs=21)
- [Technologies Used](https://www.notion.so/Batch-Data-Migration-206f25d9ba174b9f920b2ef7d0bcc133?pvs=21)
- [How It Works](https://www.notion.so/Batch-Data-Migration-206f25d9ba174b9f920b2ef7d0bcc133?pvs=21)
- [Setup Instructions](https://www.notion.so/Batch-Data-Migration-206f25d9ba174b9f920b2ef7d0bcc133?pvs=21)
- [Running the Batch Job](https://www.notion.so/Batch-Data-Migration-206f25d9ba174b9f920b2ef7d0bcc133?pvs=21)
- [Things to Note](https://www.notion.so/Batch-Data-Migration-206f25d9ba174b9f920b2ef7d0bcc133?pvs=21)
- [License](https://www.notion.so/Batch-Data-Migration-206f25d9ba174b9f920b2ef7d0bcc133?pvs=21)

## Introduction

Migrating data between databases is often required when scaling applications or transitioning to a more performant solution. In this project, we handle the migration from MySQL to PostgreSQL, dealing with differences such as `BOOLEAN` types, `SERIAL` columns, and `ENUM` handling. The project manages users, their publications, and comments.

## Technologies Used

- **Spring Boot**: Backend framework for building standalone applications.
- **Spring Batch**: Batch processing for large datasets.
- **MySQL**: Source database.
- **PostgreSQL**: Target database.
- **Flyway**: Manages migrations and database version control.
- **Docker**: For containerizing and deploying the databases.
- **Lombok**: Reduces boilerplate code for entities and services.

## How It Works

The project utilizes **Spring Batch** to divide the migration process into multiple steps:

- **User Step**: Migrates user data from MySQL to PostgreSQL.
- **Publication Step**: Migrates publications, linked to users.
- **Comment Step**: Migrates comments, linked to users and publications.

The order of execution respects data integrity constraints, ensuring that users are migrated first, followed by their related data.

## Setup Instructions

1. **Clone the repository**:
    
    ```bash
    git clone <https://github.com/spencer2k19/batch-data-migration.git>
    cd batch-data-migration
    
    ```
    
2. **Set up the environment**:
Create a `.env` file with the same entries as .env.example:
    
    ```bash
    MYSQL_URL=jdbc:mysql://localhost:3306/mydb
    MYSQL_USERNAME=username
    MYSQL_PASSWORD=password
    MYSQL_DATABASE=database
    
    POSTGRES_URL=jdbc:postgresql://localhost:5432/mydb
    POSTGRES_USERNAME=postgres
    POSTGRES_PASSWORD=password
    POSTGRES_DATABASE=database
    
    ```
    
3. **Build the project**:
    
    ```bash
    ./mvnw clean install
    
    ```
    
4. **Run the application**:
    
    ```bash
    ./mvnw spring-boot:run
    
    ```
    

## Running the Batch Job

You can start the data migration batch job via the following REST endpoint:

```
POST <http://localhost:9090/batch/start>

```

This will trigger the migration of users, publications, and comments from MySQL to PostgreSQL.

## Things to Note

- **Backup**: Before running the migration, make sure to back up your source database.
- **Database Structure**: The migration process assumes the target database structure is already created. You can use **Flyway** or another migration tool to generate the structure in PostgreSQL.

## License

This project is open-source and licensed under the MIT License. Feel free to use and contribute!

---