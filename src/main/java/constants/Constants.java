// Объявление пакета, в котором находится интерфейс
// Пакет constants - обычно содержит константы и настройки приложения
package constants;

// Объявление интерфейса Constants
// Интерфейс используется для хранения констант (финальных статических полей)
public interface Constants {


    /*
    Примеры URL для понимания структуры:

    1. HTTP URL веб-приложения:
    http://localhost:8080/cars?id=3

    2. JDBC URL для подключения к PostgreSQL:
    jdbc:postgresql://localhost:5432/cohort71_cars?user=admin&password=admin123
    */

    // Константа для пути к драйверу PostgreSQL JDBC
    // "org.postgresql.Driver" - это полное имя класса драйвера PostgreSQL
    // Эта строка передается в Class.forName() для загрузки драйвера
    // Модификаторы: public static final - стандарт для констант в интерфейсе
    public static final String DB_DRIVER_PATH = "org.postgresql.Driver";

    // Константа для базового адреса подключения к PostgreSQL
    // "jdbc:postgresql://localhost:5433/" - протокол, хост и порт
    // jdbc:postgresql:// - протокол JDBC для PostgreSQL
    // localhost - адрес сервера (можно заменить на IP)
    // 5433 - порт PostgreSQL (обычно 5432, здесь 5433 - может быть нестандартный порт)
    // Обратите внимание: строка заканчивается на "/" для соединения с именем БД
    public static final String DB_ADDRESS = "jdbc:postgresql://localhost:5433/";

    // Константа для имени базы данных
    // "cohort71_cars" - имя базы данных в PostgreSQL
    // Это та база данных, к которой будет происходить подключение
    public static final String DB_NAME = "cohort71_cars";

    // Константа для имени пользователя БД
    // "admin" - логин пользователя PostgreSQL с правами доступа к БД
    // В реальных приложениях лучше не использовать "admin" в целях безопасности
    public static final String DB_USER = "admin";

    // Константа для пароля пользователя БД
    // "admin123" - пароль пользователя PostgreSQL
    // ⚠️ ВНИМАНИЕ: Хранить пароли в открытом виде в коде - плохая практика!
    // В production лучше использовать переменные окружения или файлы конфигурации
    public static final String DB_PASSWORD = "admin123";
}
