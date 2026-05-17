

# API Contract

## Загальна інформація

Base URL:

```text
/api
```

Авторизація:

```text
Authorization: Bearer <token>
```

Система має 2 ролі користувачів:

- `USER` — створює та переглядає свої заявки на ремонт.
- `TECHNICIAN` — переглядає заявки, бере їх в роботу, змінює статус і закриває після ремонту.

---

# 1. Auth

## 1.1 Login

```http
POST /api/auth/login
```

### Request

```json
{
  "email": "ivan@example.com",
  "password": "password123"
}
```

### Response `200 OK`

```json
{
  "token": "jwt-token",
  "user": {
    "id": 1,
    "fullName": "Іван Петренко",
    "email": "ivan@example.com",
    "role": "USER"
  }
}
```

### Errors

```text
401 Unauthorized — неправильний email або пароль
```

---

# 2. User API

## 2.1 Отримати свої заявки

```http
GET /api/requests/my
```

### Response `200 OK`

```json
[
  {
    "id": 1,
    "equipmentType": "PRINTER",
    "equipmentName": "HP LaserJet",
    "title": "Принтер не друкує",
    "status": "CREATED",
    "priority": "MEDIUM",
    "createdAt": "2026-05-11T10:30:00"
  }
]
```

### Errors

```text
401 Unauthorized — користувач не авторизований
```

---

## 2.2 Створити заявку

```http
POST /api/requests
```

### Request

```json
{
  "equipmentType": "PRINTER",
  "equipmentName": "HP LaserJet",
  "title": "Принтер не друкує",
  "problemDescription": "Після запуску друку принтер не реагує",
  "userComment": "Проблема зʼявилась після заміни картриджа",
  "priority": "MEDIUM"
}
```

### Response `201 Created`

```json
{
  "id": 1,
  "userId": 1,
  "technicianId": null,
  "equipmentType": "PRINTER",
  "equipmentName": "HP LaserJet",
  "title": "Принтер не друкує",
  "problemDescription": "Після запуску друку принтер не реагує",
  "userComment": "Проблема зʼявилась після заміни картриджа",
  "technicianComment": null,
  "repairResult": null,
  "status": "CREATED",
  "priority": "MEDIUM",
  "createdAt": "2026-05-11T10:30:00",
  "updatedAt": null,
  "cancelledAt": null,
  "closedAt": null
}
```

### Errors

```text
400 Bad Request — невалідні дані
401 Unauthorized — користувач не авторизований
```

---

## 2.3 Отримати деталі заявки

```http
GET /api/requests/{id}
```

### Response `200 OK`

```json
{
  "id": 1,
  "userId": 1,
  "technicianId": 3,
  "equipmentType": "PRINTER",
  "equipmentName": "HP LaserJet",
  "title": "Принтер не друкує",
  "problemDescription": "Після запуску друку принтер не реагує",
  "userComment": "Проблема зʼявилась після заміни картриджа",
  "technicianComment": "Перевіряю драйвер",
  "repairResult": null,
  "status": "IN_PROGRESS",
  "priority": "MEDIUM",
  "createdAt": "2026-05-11T10:30:00",
  "updatedAt": "2026-05-11T11:00:00",
  "cancelledAt": null,
  "closedAt": null
}
```

### Errors

```text
401 Unauthorized — користувач не авторизований
403 Forbidden — користувач не має доступу до заявки
404 Not Found — заявку не знайдено
```

---

## 2.4 Додати або змінити коментар користувача

```http
PATCH /api/requests/{id}/user-comment
```

### Request

```json
{
  "userComment": "Проблема повторилась після перезапуску"
}
```

### Response `200 OK`

```json
{
  "id": 1,
  "userComment": "Проблема повторилась після перезапуску",
  "updatedAt": "2026-05-11T11:20:00"
}
```

### Errors

```text
401 Unauthorized — користувач не авторизований
403 Forbidden — це не заявка цього користувача
404 Not Found — заявку не знайдено
```

---

## 2.5 Скасувати заявку

```http
PATCH /api/requests/{id}/cancel
```

### Response `200 OK`

```json
{
  "id": 1,
  "status": "CANCELLED",
  "cancelledAt": "2026-05-11T11:30:00",
  "updatedAt": "2026-05-11T11:30:00"
}
```

### Errors

```text
400 Bad Request — заявку вже завершено або скасовано
401 Unauthorized — користувач не авторизований
403 Forbidden — це не заявка цього користувача
404 Not Found — заявку не знайдено
```

---

# 3. Technician API

## 3.1 Отримати всі заявки

```http
GET /api/technician/requests
```

### Query params

```text
status=CREATED
priority=HIGH
```

### Response `200 OK`

```json
[
  {
    "id": 2,
    "userId": 1,
    "technicianId": null,
    "equipmentType": "PROJECTOR",
    "equipmentName": "Epson X100",
    "title": "Проектор не вмикається",
    "status": "CREATED",
    "priority": "HIGH",
    "createdAt": "2026-05-11T09:15:00"
  }
]
```

### Errors

```text
401 Unauthorized — користувач не авторизований
403 Forbidden — користувач не є техніком
```

---

## 3.2 Взяти заявку в роботу

```http
PATCH /api/technician/requests/{id}/take
```

### Response `200 OK`

```json
{
  "id": 2,
  "technicianId": 3,
  "status": "IN_PROGRESS",
  "updatedAt": "2026-05-11T11:40:00"
}
```

### Errors

```text
400 Bad Request — заявку вже взяв інший технік або її не можна взяти
401 Unauthorized — користувач не авторизований
403 Forbidden — користувач не є техніком
404 Not Found — заявку не знайдено
```

---

## 3.3 Змінити статус заявки

```http
PATCH /api/technician/requests/{id}/status
```

### Request

```json
{
  "status": "WAITING_FOR_USER"
}
```

### Дозволені значення `status`

```text
IN_PROGRESS
WAITING_FOR_USER
```

Статуси `COMPLETED` та `CANCELLED` не можна встановлювати через цей endpoint.
`COMPLETED` встановлюється тільки через закриття заявки, а `CANCELLED` — тільки через скасування заявки користувачем.

### Response `200 OK`

```json
{
  "id": 2,
  "status": "WAITING_FOR_USER",
  "updatedAt": "2026-05-11T12:00:00"
}
```

### Errors

```text
400 Bad Request — невалідна зміна статусу
401 Unauthorized — користувач не авторизований
403 Forbidden — це не заявка цього техніка
404 Not Found — заявку не знайдено
```

---

## 3.4 Додати або змінити коментар техніка

```http
PATCH /api/technician/requests/{id}/technician-comment
```

### Request

```json
{
  "technicianComment": "Потрібно уточнити, коли саме виникає проблема"
}
```

### Response `200 OK`

```json
{
  "id": 2,
  "technicianComment": "Потрібно уточнити, коли саме виникає проблема",
  "updatedAt": "2026-05-11T12:10:00"
}
```

### Errors

```text
401 Unauthorized — користувач не авторизований
403 Forbidden — це не заявка цього техніка
404 Not Found — заявку не знайдено
```

---

## 3.5 Додати результат ремонту

```http
PATCH /api/technician/requests/{id}/repair-result
```

### Request

```json
{
  "repairResult": "Замінено кабель живлення, обладнання працює стабільно"
}
```

### Response `200 OK`

```json
{
  "id": 2,
  "repairResult": "Замінено кабель живлення, обладнання працює стабільно",
  "updatedAt": "2026-05-11T12:30:00"
}
```

### Errors

```text
400 Bad Request — результат ремонту порожній
401 Unauthorized — користувач не авторизований
403 Forbidden — це не заявка цього техніка
404 Not Found — заявку не знайдено
```

---

## 3.6 Закрити заявку

```http
PATCH /api/technician/requests/{id}/close
```

### Request

```json
{
  "repairResult": "Замінено кабель живлення, обладнання працює стабільно"
}
```

### Response `200 OK`

```json
{
  "id": 2,
  "status": "COMPLETED",
  "repairResult": "Замінено кабель живлення, обладнання працює стабільно",
  "closedAt": "2026-05-11T12:45:00",
  "updatedAt": "2026-05-11T12:45:00"
}
```

### Errors

```text
400 Bad Request — результат ремонту порожній або заявку вже закрито
401 Unauthorized — користувач не авторизований
403 Forbidden — це не заявка цього техніка
404 Not Found — заявку не знайдено
```

---

# 4. Enums

## 4.1 `equipmentType`

```text
PRINTER
LAPTOP
COMPUTER
PROJECTOR
ROUTER
MONITOR
OTHER
```

## 4.2 `status`

```text
CREATED
IN_PROGRESS
WAITING_FOR_USER
COMPLETED
CANCELLED
```

## 4.3 `priority`

```text
LOW
MEDIUM
HIGH
```

## 4.4 `role`

```text
USER
TECHNICIAN
```

---

# 5. Бізнес-правила

- Користувач може переглядати тільки свої заявки.
- Користувач може додавати або змінювати коментар тільки у своїй заявці.
- Користувач може скасувати тільки свою заявку.
- Користувач не може змінювати або скасовувати заявку зі статусом `COMPLETED` або `CANCELLED`.
- Технік може переглядати список усіх заявок.
- Технік може переглядати деталі будь-якої заявки.
- Технік може взяти в роботу тільки заявку зі статусом `CREATED`.
- Після взяття заявки в роботу статус змінюється на `IN_PROGRESS`, а `technicianId` заповнюється поточним техніком.
- Технік може змінювати тільки ті заявки, які він взяв у роботу.
- Endpoint зміни статусу техніком може встановлювати тільки `IN_PROGRESS` або `WAITING_FOR_USER`.
- Статус `COMPLETED` встановлюється тільки через endpoint закриття заявки.
- Статус `CANCELLED` встановлюється тільки через endpoint скасування заявки користувачем.
- Закрити заявку можна тільки з непорожнім `repairResult`.
- При закритті заявки статус стає `COMPLETED`, а поле `closedAt` заповнюється поточною датою.
- При скасуванні заявки статус стає `CANCELLED`, а поле `cancelledAt` заповнюється поточною датою.