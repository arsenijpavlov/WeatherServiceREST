# Задача Служба погоды
Напишите службу REST с одной конечной точкой /weather При вызове этот сервис должен запросить в базе данных текущую погоду (температуру) в таблице «weather_history». Если на текущую дату в базе данных не найдено ни одной записи, то необходимо считать текущее значение температуры со страницы «yandex.ru». После считывания температуры он должен вставить новую запись в «weather_history». В конце концов, он должен вернуть пользователю значение температуры.

---
**Примечания:**
- Вы можете использовать любую среду Java для создания веб-службы (например, Spring).
- Вы можете использовать любую базу данных для хранения исторических данных (например, PostgreSQL).
- Для взаимодействия с базой данных можно использовать интерфейсы JDBC или JPA.
- Для чтения веб-страницы и извлечения значений температуры используйте стандартные классы Java (нет необходимости использовать дополнительную библиотеку).
- Таблица Weather_history состоит из двух столбцов:
  - погода_дата ДАТА
  - погода_значение VARCHAR