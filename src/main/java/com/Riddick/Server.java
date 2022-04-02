package com.Riddick;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.net.HttpURLConnection.HTTP_OK;

/*Задача Служба погоды
* Напишите службу REST с одной конечной точкой /weather
* При вызове этот сервис должен запросить в базе данных текущую погоду (температуру) в таблице «weather_history».
* Если на текущую дату в базе данных не найдено ни одной записи, то необходимо считать текущее значение температуры
* со страницы «yandex.ru». После считывания температуры он должен вставить новую запись в «weather_history».
* В конце концов, он должен вернуть пользователю значение температуры.
*
* Примечания:
* Вы можете использовать любую среду Java для создания веб-службы (например, Spring).
* Вы можете использовать любую базу данных для хранения исторических данных (например, PostgreSQL).
* Для взаимодействия с базой данных можно использовать интерфейсы JDBC или JPA.
* Для чтения веб-страницы и извлечения значений температуры используйте стандартные классы Java (нет необходимости
* использовать дополнительную библиотеку).
* Таблица Weather_history состоит из двух столбцов:
*   погода_дата ДАТА
*   погода_значение VARCHAR
*/

@WebService(targetNamespace = "http://weather")
public class Server {
    static String URLSQL = "jdbc:mysql://localhost/weather_database";
    static String user = "root";
    static String pass = "Root";

    static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(URLSQL, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Server() throws SQLException {
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        Endpoint endpointServer = Endpoint.publish("http://localhost/weather", new Server());
    }

    /*
     * Функция запроса текущей погоды
     */
    public String getWeatherFromTable() throws SQLException, IOException {
        String date = (new SimpleDateFormat("\"YYYY-MM-dd\"")).format(new Date());
        String sql = "select temperature from weather_history where weather_date like " + date;
        String sqlCount = "select count(*) as lol from weather_history where weather_date like " + date;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlCount);
        ResultSet res;
        if (resultSet.next())
            if ((res = statement.executeQuery(sql)).next())
                return "Сегодня за окном: " + res.getInt("temperature") + "'";
            else {
                if (addRecord(date))
                    if ((res = statement.executeQuery(sql)).next())
                        return "Сегодня за окном (получено с Yandex.ru): " + res.getInt("temperature") + "'";
                else
                    return "Ошибка запроса и(или) записи текущей погоды";
            }
        return "Error";
    }

    /*
    * Функия добавления текущей погоды в таблицу weather_database.weather_history
     */
    public boolean addRecord(String date) throws SQLException, IOException {
        String sql = "insert into weather_history (weather_date, temperature)" +
                " values (" + date + ", " + stringToInteger(getWeather()) + ")";
        Statement statement = connection.createStatement();
        int res = statement.executeUpdate(sql);
        return res == 1;
    }

    /*
    * Функция получения текущего значения погоды
     */
    public String getWeather() throws IOException {
        double latGrad = 59.59752396708402;//широта в градусах
        double lonGrad = 54.31358303882284;//долгота в градусах
        final URL urlAPI = new URL("https://api.weather.yandex.ru/v2/informers?" +
                "lat=" + latGrad +
                "&lon=" + lonGrad +
                "&lang=ru_RU");
        String keyAPI = "46c5d225-b001-49c8-8af3-a94974cfea30";

        HttpURLConnection httpURLConnection = (HttpURLConnection) urlAPI.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("X-Yandex-API-Key", keyAPI);
        httpURLConnection.setDoOutput(true);    //можно убрать, ибо по умолчанию так

        int responseCode = httpURLConnection.getResponseCode();
        if (responseCode == HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inLine;
            StringBuffer sBuffer = new StringBuffer();

            while ((inLine = reader.readLine()) != null) {
                if (inLine.contains("fact")) {
                    sBuffer.append(inLine);
                }
            }
            reader.close();
            return getStringFromJSON(sBuffer.toString(), "temp");
        } else
            return "Response = " + responseCode;
    }

    /*
     * найти значение ключа во входящей JSON-подобной строке
     * находит только первое вхождение последовательности символов key
     */
    static String getStringFromJSON(String inString, String key) {
        if (!inString.contains(key)) {
            return "Key not found";
        } else {
            int counter = 0;
            int i = 0;
            for (; i < inString.length(); i++) {
                if (key.length() > counter) {
                    if (inString.charAt(i) == key.charAt(counter))
                        counter++;
                    else
                        counter = 0;
                } else
                    break;
            }
            //убираем доп. знаки перед значением
            i += 2;
            String answer = "";
            Character c;
            while ((c = inString.charAt(i)) != ',') {
                answer += String.valueOf(c);
                i++;
            }
            return answer;
        }
    }

    /*
     * Функция преобразования строки в число
     */
    static int stringToInteger(String inString) {
        int sign = 1;
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i = 0; i < inString.length(); i++) {
            if (i == 0 && inString.charAt(i) == '-')
                sign *= -1;
            else {
                switch (inString.charAt(i)) {
                    case '0':
                        arr.add(0);
                        break;
                    case '1':
                        arr.add(1);
                        break;
                    case '2':
                        arr.add(2);
                        break;
                    case '3':
                        arr.add(3);
                        break;
                    case '4':
                        arr.add(4);
                        break;
                    case '5':
                        arr.add(5);
                        break;
                    case '6':
                        arr.add(6);
                        break;
                    case '7':
                        arr.add(7);
                        break;
                    case '8':
                        arr.add(8);
                        break;
                    case '9':
                        arr.add(9);
                        break;
                    default:
                        return -9999;
                }
            }
        }
        int value = 0;
        for (int i = 0; i < arr.size(); i++) {
            if (value == 0)
                value = arr.get(i);
            else
                value = value * 10 + arr.get(i);
        }
        return sign * value;
    }
}