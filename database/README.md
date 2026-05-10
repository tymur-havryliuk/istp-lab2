# Database Setup

У цій директорії зібрана окрема конфігурація PostgreSQL для DB-first підходу: схема створюється SQL-скриптами з `init/`, а backend має лише підключатися до вже готової бази.

## Команди

Запуск БД:

```bash
docker compose up -d
```

Зупинка БД:

```bash
docker compose down
```

Повне очищення разом з volume:

```bash
docker compose down -v
```

Підключення до БД через `psql`:

```bash
docker exec -it maintenance_service_db psql -U postgres -d maintenance_service
```
