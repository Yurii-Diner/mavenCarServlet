// Объявление пакета, в котором находится класс
package repository;

// Импорт класса Car из модели

import model.Car;

// Импорт классов для работы с большими числами (цена автомобиля)
import java.math.BigDecimal;

// Импорт классов JDBC (Java Database Connectivity) для работы с БД
import java.sql.*;

// Импорт класса для работы со списками
import java.util.ArrayList;
import java.util.List;

// Импорт статических констант из класса Constants
// Предполагается, что там хранятся настройки подключения к БД
import static constants.Constants.*;

// Класс реализует интерфейс CarRepository для работы с PostgreSQL
public class CarRepositoryPostgress implements CarRepository {

    // Комментарий о формате URL для подключения к PostgreSQL
    // jdbc:postgresql://localhost:5432/cohort71_cars?user=admin&password=admin123

    // Этот метод предназначен для создания соединения с БД
    private Connection getConnection() {
        try {

            // При помощи рефлексии загружаем драйвер PostgreSQL в память JVM
            // В качества аргумента передаем путь к этому драйверу
            // DB_DRIVER_PATH = "org.postgresql.Driver"
            Class.forName(DB_DRIVER_PATH);

            // Формируем строку подключения (URL) к БД
            // DB_ADDRESS - например "jdbc:postgresql://localhost:5433/"
            // DB_NAME - например "cohort71_cars"
            // DB_USER - имя пользователя
            // DB_PASSWORD - пароль
            String dbUrl = String.format("%s%s?user=%s&password=%s",
                    DB_ADDRESS, DB_NAME, DB_USER, DB_PASSWORD);

            // Создаем и возвращаем соединение с БД
            // DriverManager управляет всеми соединениями
            return DriverManager.getConnection(dbUrl);

        } catch (Exception e) {
            // Преобразуем проверяемое исключение в непроверяемое RuntimeException
            // чтобы не объявлять throws в сигнатуре метода
            throw new RuntimeException(e);
        }
    }


    // Метод для сохранения нового автомобиля в БД
    @Override
    public Car save(Car car) {
        // try-with-resources: автоматически закроет Connection после блока try
        // Даже если произойдет исключение
        try (Connection connection = getConnection()) {

            // Формируем SQL-запрос для вставки данных
            // Используем форматирование строки для безопасности от SQL-инъекций
            // (но лучше использовать PreparedStatement)
            String query = String.format(
                    "INSERT INTO car (brand, year, price) values('%s', %d, %s);",
                    car.getBrand(),     // Марка автомобиля (строка)
                    car.getYear(),      // Год выпуска (число)
                    car.getPrice()      // Цена (BigDecimal преобразуется в строку)
            );

            // Создаем Statement для выполнения SQL-запросов
            // Statement - это объект, который отправляет SQL в БД
            Statement statement = connection.createStatement();

            // Выполняем запрос на вставку
            // Statement.RETURN_GENERATED_KEYS - указываем, что нужно вернуть
            // автоматически сгенерированные ключи (в нашем случае id)
            statement.execute(query, Statement.RETURN_GENERATED_KEYS);

            // Получаем ResultSet с сгенерированными ключами
            // ResultSet - это таблица с результатами запроса
            ResultSet resultSet = statement.getGeneratedKeys();

            // Перемещаем курсор на первую строку результата
            // next() возвращает true если строка существует
            resultSet.next();

            // Получаем значение сгенерированного id из колонки "id"
            Long id = resultSet.getLong("id");

            // Устанавливаем id в объект Car
            car.setId(id);

        } catch (Exception e) {
            // Обрабатываем исключения БД
            throw new RuntimeException(e);
        }

        // Возвращаем автомобиль с установленным id
        return car;
    }


    // Метод для получения автомобиля по id
    @Override
    public Car getById(long id) {
        // формируем строку-запрос к базе данных
        // ? - это параметризованный placeholder (заполнитель)
        // На месте ? будет подставлено значение, которое этот метод принимает
        String sqlQuery = "select * from car where id = ?";

        // делаем соединение, которое мы реализовывали в методе getConnection()
        // try-with-resources: автоматически закроет Connection и PreparedStatement
        // Connection и PreparedStatement реализуют интерфейс AutoCloseable
        try (Connection connection = getConnection();
             // Создаем шаблон для запроса в базу данных. Однако в этом шаблоне нам
             // нужно будет заменить знак вопроса на id
             // prepareStatement() компилирует SQL запрос и создает PreparedStatement
             PreparedStatement ps = connection.prepareStatement(sqlQuery)
        ) {
            // Корректируем наш запрос
            // Этот метод предназначен для того чтобы подменить знак вопроса
            // на значение id который в свою очередь имеет тип Long
            // единичка означает номер знака вопроса который будет найден
            // в этой строоке "delete from car where id = ?"
            // первое совпадение со знаком вопроса будет подменено на id
            // 1 - индекс параметра (начинается с 1, не с 0!)
            // id - значение, которое будет безопасно подставлено вместо ?
            // Драйвер БД сам экранирует специальные символы
            ps.setLong(1, id);

            // executeQuery() - метод предназначен для чтения данных из базы данных
            // он отправляет SQL-запрос и возвращает данные в виде объекта ResultSet
            // Объект ResultSet, содержит строки и столбцы из базы данных.
            // ResultSet это чтение а это значит что мы открываем поток в блоке try
            // для того чтобы он автоматически закрылся после блока catch
            try (ResultSet rs = ps.executeQuery()) {
                // Объект rs содержит строки и столбцы из базы данных в нашем случае
                // Это одна строка с тем id который мы получили на входе
                // rs.next() перемещает курсор к первой строке результата
                // Возвращает true если строка существует, false если результат пуст
                // Важно: изначально курсор находится ПЕРЕД первой строкой
                // rs.next() перемещает курсор на следующую строку Возвращает true, если строка
                // существует. В нашем случае мы проверяем одну единственную строку на предмет
                // совпадения с переданным на вход этого метода id
                if (rs.next()) {
                    // Если автомобиль найден - преобразуем ResultSet в Car
                    // вспомогательный метод mapCar() мы создали в этом же классе
                    return mapCar(rs);
                } else {
                    // Если ResultSet пустой - автомобиль не найден
                    // возвращаем null или пробрасываем исключение для более информативного
                    // ответа
                    throw new RuntimeException("Автомобиль с таким id не был найден = " + id +
                    "Класс: CarRepositoryPostgress \n" +" Метод: getById() \n " +
                            "Метод rs.next() не обнаружил такого id"

                    );
                }
            }

            // ResultSet автоматически закрывается здесь
        } catch (SQLException e) {
            // Более информативное сообщение об ошибке
            // Включает id, по которому искали
            throw new RuntimeException("Автомобиль с таким id не был найден = " + id +
                    "Класс: CarRepositoryPostgress \n" +" Метод: getById() \n "

            );
        }
        // Connection и PreparedStatement автоматически закрываются здесь
    }


    // Метод для обновления автомобиля
    @Override
    public Car update(Car car) {

        // Делаем строку-шаблон для запроса в базу данных с целью обновления
        // строки с автомобилем, который передается на вход
        String sqlQuery = "UPDATE car SET brand=?, year=?, price=? WHERE id=?";

        // Создаем поток подключения к базе данных
        try (Connection connection = getConnection();
             // передаем шаблон строки для обновления
             PreparedStatement ps = connection.prepareStatement(sqlQuery)
        ) {

            // корректируем шаблон строки подменяя знаки вопроса на информацию об
            // автомобиле
            ps.setString(1, car.getBrand());
            ps.setInt(2, car.getYear());
            ps.setBigDecimal(3, car.getPrice());
            ps.setLong(4, car.getId());

            // Метод executeUpdate() предназначен для обновления данных в базе данных
            // Он дает нам обратную связь в виде значения int сообщая количество
            // измененных в результате его работы строк. Эту информацию можно
            // использовать для дальнейших проверок. Если например количество
            // измененных строк равно нулю, то можно сообщить об этом пробросив
            // соотвествующее исключение
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new RuntimeException("Не удалось обновить автомобиль с id " + car.getId());
            }

            // Если все прошло успешно, и программа добралась до этого момента
            // то возвращаем тот-же объект который получили на входе
            // Хотя наверное можно было бы прочитать его из базы данных и вернуть
            // Но не хочется лишний раз дергать базу данных
            return car;

        } catch (Exception e) {
            // Преобразуем проверяемое исключение в непроверяемое RuntimeException
            // чтобы не объявлять throws в сигнатуре метода
            throw new RuntimeException(e);
        }

    }

    // Метод для удаления автомобиля по id
    @Override
    public Car delete(long id) {

        // получаем по id автомобиль который нужно удалить. Для этого используем
        // ранее создынный метод getById(id)
        Car carToRemove = getById(id);

        // Если такого автомобиля не существует, то сразу выходим из этого
        // метода пробрасываем исключение
        if (carToRemove == null) {
            throw new IllegalArgumentException("Автомобиль с id " + id + " не найден");
            // return null;
        }

        // Если id существует то формируем строку-запрос в базу данных для удаления
        String sqlQuery = "delete from car where id = ?";

        // Соединяемся с базой данных
        try (Connection connection = getConnection();
             // передаем нашу строку-запрос на удаление
             PreparedStatement ps = connection.prepareStatement(sqlQuery)
        ) {

            // Этот метод предназначен для того чтобы подменить знак вопроса
            // на значение id который в свою очередь имеет тип Long
            // единичка означает что эта строка "delete from car where id = ?"
            // будет проверятся посимвольно и
            // первое совпадение со знаком вопроса будет подменено на id
            ps.setLong(1, id);

            // Метод executeUpdate() предназначен для обновления данных в базе данных
            // Он дает нам обратную связь в виде значения int сообщая количество
            // измененных в результате его работы строк. Эту информацию можно
            // использовать для дальнейших проверок. Если например количество
            // измененных строк равно нулю, то можно сообщить об этом пробросив
            // соотвествующее исключение
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted == 0) {
                throw new RuntimeException("Не удалось удалить автомобиль с id " + id);
            }

            // если все-таки изменения в базе данных произошли, то можно вернуть
            // удаленный автомобиль
            return carToRemove;

        } catch (Exception e) {
            // Преобразуем проверяемое исключение в непроверяемое RuntimeException
            // чтобы не объявлять throws в сигнатуре метода
            throw new RuntimeException("Не удалось удалить автомобиль с id " + id);
        }
    }

    // Метод для получения всех автомобилей (пока не реализован)
    @Override
    public List<Car> getAll() {
        // Создаем список для автомобилей
        List<Car> cars = new ArrayList<>();

        // Создаем строк запрос в базу данных
        String sql = "SELECT * FROM car ORDER BY id";

        // Открываем поток для чтения
        try (Connection connection = getConnection();
             // передаем строку запрос
             PreparedStatement ps = connection.prepareStatement(sql);
             // читаем базу данных
             ResultSet rs = ps.executeQuery()) {

            // При помощи цикла обходим и метода next() обходим базу данных
            // next() - похож на итератор возвращает true если строка существует
            // Обходим базу данных до тех пор пока строки существуют
            while (rs.next()) {

                // Добавля в список объект автомобиля
                cars.add(mapCar(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Не удалось подключиться к базе данных ", e);
        }

        return cars;
    }


    // Приватный вспомогательный метод для преобразования ResultSet в Car
    private Car mapCar(ResultSet rs) throws SQLException {
        // Извлекаем значение колонки "id" как Long
        // getLong() возвращает примитив long, но может быть NULL в БД
        // rs.wasNull() можно использовать для проверки NULL
        Long dbId = rs.getLong("id");

        // Извлекаем значение колонки "brand" как String
        String brand = rs.getString("brand");

        // Извлекаем значение колонки "year" как int
        int year = rs.getInt("year");

        // Извлекаем значение колонки "price" как BigDecimal
        // BigDecimal идеально подходит для денежных значений
        BigDecimal price = rs.getBigDecimal("price");

        // Создаем и возвращаем новый объект Car
        return new Car(dbId, brand, year, price);
    }
}