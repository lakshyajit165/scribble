### Command to start postgres db on docker

- docker run --name notesdb -e POSTGRES_PASSWORD=postgres123 -d postgres

### Command to run the app through docker compose after building all the projects
> postgres db not included yet
- docker-compose --env-file .env up --build -d

### Command to just run the app through docker compose
> postgres db not included yet
- docker-compose --env-file .env up -d