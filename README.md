# Hotel Booking Microservices

Система бронирования отелей на основе микросервисной архитектуры. Проект в разработке.

## Стек технологий

- Java 21
- Spring Boot 3.3 / Spring Cloud 2023.0
- Spring Security + JWT
- PostgreSQL
- Apache Kafka + Avro + Schema Registry
- Redis
- Elasticsearch
- Netflix Eureka (Service Discovery)
- Spring Cloud Gateway
- MapStruct
- Liquibase
- Docker

## Архитектура

```
                        ┌──────────────┐
                        │ API Gateway  │ :8080
                        └──────┬───────┘
                               │
                 ┌─────────────┼─────────────┐
                 │             │             │
          ┌──────▼──────┐ ┌───▼────┐  ┌─────▼──────┐
          │ Auth Service│ │  User  │  │  Booking   │
          │             │ │Service │  │  Service   │
          └──────┬──────┘ └───┬────┘  └────────────┘
                 │            │
                 └─────┬──────┘
                       │
                 ┌─────▼─────┐
                 │   Kafka   │
                 └───────────┘
```

Все сервисы регистрируются в **Eureka Server** (:8761) для обнаружения друг друга. API Gateway маршрутизирует запросы и валидирует JWT-токены.

## Сервисы

| Сервис | Порт | Статус | Описание |
|---|---|---|---|
| eureka-server | 8761 | готов | Service Discovery |
| api-gateway | 8080 | готов | Маршрутизация, JWT-валидация, CORS |
| auth-service | dynamic | готов | Регистрация, логин, генерация JWT |
| user-service | dynamic | готов | Управление профилями пользователей |
| booking-service | - | в планах | Бронирование отелей |
| payment-service | - | в планах | Обработка платежей |
| notification-service | - | в планах | Уведомления |
| search-service | - | в планах | Поиск отелей |

## API

### Auth Service

```
POST /api/auth/v1/auth/register    регистрация
POST /api/auth/v1/auth/login       авторизация
```

### User Service

```
GET    /api/users/v1/user/data     получить профиль
PUT    /api/users/v1/user          обновить профиль
DELETE /api/users/v1/user          удалить профиль
```

Все запросы к User Service требуют заголовок `Authorization: Bearer <token>`.

## Event-Driven взаимодействие

Сервисы общаются через Kafka с использованием Avro-схем и Schema Registry.

**Топик:** `user-events`

**Типы событий:**
- `USER_REGISTERED` - auth-service публикует после регистрации
- `USER_CREATED` - user-service подтверждает создание профиля
- `USER_CREATION_FAILED` - user-service сообщает об ошибке
- `USER_UPDATED` - профиль обновлен
- `USER_DELETED` - профиль удален

Реализован паттерн **SAGA** для согласованности данных между сервисами. Если создание профиля в user-service не удалось, auth-service откатывает запись учетных данных.

## Аутентификация

1. Пользователь регистрируется/логинится через auth-service
2. Auth-service генерирует JWT-токен и кэширует его в Redis (TTL 24ч)
3. Клиент отправляет токен в заголовке `Authorization`
4. API Gateway валидирует токен и пробрасывает `X-User-Id` и `X-User-Roles` в downstream-сервисы
5. Downstream-сервисы доверяют заголовкам от gateway

## Запуск

### Требования

- Java 21
- Docker / Docker Compose
- PostgreSQL (порт 5433)
- Kafka (порт 9092)
- Schema Registry (порт 8085)
- Redis (порт 6379)

## Структура проекта

```
hotel-booking-microservices/
├── eureka-server/           Service Discovery
├── api-gateway/             API Gateway
├── auth-service/            Аутентификация
├── user-service/            Пользователи
├── booking-service/         Бронирования (WIP)
├── payment-service/         Платежи (WIP)
├── notification-service/    Уведомления (WIP)
├── search-service/          Поиск (WIP)
├── build.gradle             Корневой билд-файл
└── settings.gradle          Определение модулей
```

## Статус

Проект в активной разработке. Реализована основа: service discovery, gateway, аутентификация, управление пользователями и event-driven коммуникация. Далее -- бронирования, платежи, поиск и уведомления.
