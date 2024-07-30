# CloudFileStorage
### [Read in English](README.md)
## Оглавление
- [Стек](#стек)
- [Описание проекта](#описание-проекта)
- [Валидация](#валидация)
  - [Логин и регистрация](#логин-и-регистрация)
  - [Папки и файлы](#папки-и-файлы)
- [Интерфейс](#интерфейс)
- [Инструкция по запуску](#инструкция-по-запуску)
  - [С помощью Docker](#с-помощью-docker)


## Стек
- Java
- Gradle
- Spring (Web, Data Jpa, Security)
- PostgreSQL
- S3 Minio
- Redis
- Docker
- Testing
	- JUnit
	- Mockito 
	- AssertJ
  - Testcontainers
- Frontend
  - JS
  - Thymeleaf
  - Bootstrap
- Прочие библиотеки
	- [Mapstruct](https://mapstruct.org/)

<br>

## Описание проекта
Многопользовательское файловое облако. Пользователи сервиса могут использовать его для загрузки и хранения файлов.
<br>
Функционал:
- Создание пустой папки
- Загрузка, удаление, переименование файлов и папок
- Поиск по всему хранилищу

<br>

## Валидация:
### Логин и регистрация
- Имя пользователя должно быть свободно
- Имя пользователя не должно быть пустым
- Минимальная длина пароля - 5 символов
- Пароли при регистрации должны совпадать

<br>

### Папки и файлы
- При создании пустой папки имя папки не должно быть пустым
- При переименовании имена файлов и папок не должны быть пустыми или использовать уже занятые названия
- Максимальный размер загружаемых файлов/папок - 500MB

<br>

## Интерфейс
![image](https://github.com/user-attachments/assets/4ba92684-08ca-48af-a0dc-d61d14504eb7)
![image](https://github.com/user-attachments/assets/cbdc74ad-476f-4030-8727-06b20c0ce7e0)
![image](https://github.com/user-attachments/assets/c769a4a9-c83c-4afd-88c8-1a205930e65b)
![image](https://github.com/user-attachments/assets/ff52aa46-9e54-4e3d-a30e-4de5b8c62fe6)
![image](https://github.com/user-attachments/assets/1722ef84-db41-4c58-a7f0-3271bb033e73)
![image](https://github.com/user-attachments/assets/f82ce383-3af3-4936-9e04-d95bcbbb4b26)

<br>

## Инструкция по запуску
**Важно**: внешние порты, используемые в docker-compose файлах могут быть заняты на вашей системе. Для исправления необходимо в ручную установить свободный порт в используемом docker-compose. 
В docker-compose.yml можно просто изменить порт у сервиса app (напр., "8001:8080" -> "8222":"8080")


### С помощью Docker
1. Клонировать репозиторий:
    ```sh
    git clone https://github.com/ryzendee/cloudfilestorage
    ```
2. Перейти в директорию приложения:
    ```sh
    cd cloudfilestorage
    ```
3. Запустить docker-compose (по желанию добавить -d для запуска в фоновом режиме):
    ```sh
    docker compose up
    ```
