# API

## File storing service

| Команда | Запрос                                 | Возврат                     | Примечание                                                                   |
|---------|----------------------------------------|-----------------------------|------------------------------------------------------------------------------|
| GET     | `api/storage/documents/{documentId}`   | Один файл                   | Получение документа по ID                                                    |
| GET     | `api/storage/documents?studentId={id}` | Массив документов           | Получение всех документов по ID студента                                     |
| GET     | `api/storage/students?name={name}`     | Массив документов           | Получение всех документов по имени студента                                  |
| GET     | `api/storage/documents/{id}/raw`       | Binary stream               | Скачивание файла                                                             |
| GET     | `api/storage/documents`                | Массив всех документов      | Получение списка всех документов в базе                                      |
| GET     | `api/storage/students`                 | Массив всех студентов       | Получение списка всех студентов в базе                                       |
| POST    | `api/storage/documents`                | JSON метаданных             | Добавление документа (и автоматически добавление студента, если его еще нет) |
| POST    | `api/storage/students`                 | JSON метаданных             | Добавление студента (если его еще нет)                                       |
| PUT     | `api/storage/documents/{documentId}`   | Измененный данные документа | Редактирование данных документа                                              |
| PUT     | `api/storage/students/{studentId}`     | Измененные данные студента  | Редактирование данных студента                                               |
| DELETE  | `api/storage/documents/{documentId}`   | -                           | Удаление документа                                                           |
| DELETE  | `api/storage/students/{studentId}`     | -                           | Удаление студента (каскадное удаление его документов)                        |

## File analysis service

| Команда | Запрос                                        | Возврат                   | Примечание                                     |
|---------|-----------------------------------------------|---------------------------|------------------------------------------------|
| GET     | `api/analysis/overview`                       | Отчет обо всех документах | Получение отчета со всеми документами          |
| GET     | `api/analysis/overview/{id}`                  | Отчет об одном документе  | Получение отчета по его id                     |
| GET     | `api/analysis/overview/{id}/cloud`            | Облако слов на картинке   | Получение картинки с облаком слов              |
| GET     | `api/analysis/overview/{documentId}/document` | Отчет об одном документе  | Получение отчета о документе по его id         |
| POST    | `api/analysis/launch`                         | JSON метаданных           | Запуск анализа документа                       |

## Database

### File storing service

#### Students

| Атрибут    | Тип данных | Примечание        | 
|------------|------------|-------------------|
| id         | BigInt     | ID студента       |
| last_name  | Varchar    | Фамилия студента  |
| first_name | Varchar    | Имя студента      |
| patronymic | Varchar    | Отчество студента |
| group      | Varchar    | Группа            |

#### Documents

| Атрибут     | Тип данных | Примечание         | 
|-------------|------------|--------------------|
| id          | BigInt     | ID документа       |
| file_path   | Varchar    | Путь к документу   |
| file_name   | Varchar    | Название документа |
| student_id  | BigInt     | FK, ID студента    |
| upload_date | Timestamp  | Дата добавления    |

### File analysis service

#### Reports

| Атрибут       | Тип данных | Примечание       | 
|---------------|------------|------------------|
| id            | BigInt     |                  |
| document_id   | BigInt     | ID документа     |
| file_size     | BigInt     | Размер документа |
| file_format   | Varchar    | Формат документа |
| status_id     | BigInt     | FK, ID статуса   |
| error_message | Varchar    | Сообщение        |
| word_cloud    | Bytea      | Облако слов      |

#### Status

| Атрибут | Тип данных | Примечание | 
|---------|------------|------------|
| id      | BigInt     | ID статуса |
| status  | Varchar    | Статус     |