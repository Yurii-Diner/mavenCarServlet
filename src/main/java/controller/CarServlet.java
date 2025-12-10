// Пакет, в котором находится класс
package controller;

// Импорты необходимых библиотек
import com.fasterxml.jackson.databind.ObjectMapper;  // Для работы с JSON
import jakarta.servlet.ServletException;            // Исключения сервлетов
import jakarta.servlet.http.HttpServlet;            // Базовый класс сервлета
import jakarta.servlet.http.HttpServletRequest;     // Объект HTTP-запроса
import jakarta.servlet.http.HttpServletResponse;    // Объект HTTP-ответа
import model.Car;                                   // Модель автомобиля

import repository.CarRepository;
import repository.CarRepositoryPostgress;

import java.io.IOException;                         // Исключения ввода-вывода
import java.util.List;                              // Для работы со списками
import java.util.Map;                               // Для работы с Map (параметры)

// Класс сервлета для работы с автомобилями
// Наследуется от HttpServlet - это обязательно для сервлетов
public class CarServlet extends HttpServlet {

    // Репозиторий для работы с данными об автомобилях
    // Это наше "хранилище" данных (обычно база данных, здесь - Map)
    //private CarRepository carRepository = new CarRepositoryMap();
    private CarRepository carRepository = new CarRepositoryPostgress();

    // ObjectMapper из библиотеки Jackson для преобразования
    // Java-объектов в JSON и обратно
    private ObjectMapper mapper = new ObjectMapper();

     // Методы сервлета (doGet(), doPost(), doPut(), doDelete()) — это обработчики HTTP-команд от клиента.
     // doGet()	    Получить данные	GET
     // doPost()    Создать новый ресурс
     // doPut()	    Обновить ресурс
     // doDelete()  Удалить ресурс


    // ============================================================
    // Метод для обработки HTTP GET запросов.
    // Получаем запрос от клиентаИ даем ему ответ.
    // ============================================================
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        /*
        List<Car> cars = carRepository.getAll();

        cars.forEach(car -> {
            try {
                response.getWriter().write(car.toString() + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        */

        // Два варианта GET-запроса:
        // 1) GET http://10.2.3.4:8080/cars - все машины
        // 2) GET http://10.2.3.4:8080/cars?id=3&color=red - машина по id

        // request - объект запроса. Содержит все данные от клиента
        // response - объект ответа. В него записываем данные для клиента

        // У класса HttpServletResponse есть втроенный метод setContentType()
        // В котором мы указываем что хотим отправлять данные в формате JSON
        response.setContentType("application/json");
        // Устанавливаем кодировку UTF-8 для корректной работы с русским текстом
        response.setCharacterEncoding("UTF-8");

        // Получаем все параметры запроса в виде Map
        // Пример: запрос cars?id=3&color=red даст Map:
        // {
        //   "id" : ["3"],
        //   "color" : ["red"]
        // }
        Map<String, String[]> params = request.getParameterMap();

        // Проверяем, есть ли параметры в запросе
        if (params.isEmpty()) {
            // ============================================
            // СЛУЧАЙ 1: Нет параметров → возвращаем ВСЕ машины
            // ============================================

            // Получаем все автомобили из репозитория
            List<Car> cars = carRepository.getAll();

            // Преобразуем список автомобилей в JSON-строку
            // Пример: [{"id":1,"model":"Toyota"},{"id":2,"model":"BMW"}]
            String jsonResponse = mapper.writeValueAsString(cars);

            // Записываем JSON-строку в тело ответа
            response.getWriter().write(jsonResponse);

        } else {
            // ============================================
            // СЛУЧАЙ 2: Есть параметры → ищем конкретную машину
            // ============================================

            // Получаем значение параметра "id" из Map
            // params.get("id") возвращает массив String[], берем первый элемент [0]
            String idString = params.get("id")[0];  // Например: "3"


            // Преобразуем строку в число (long)
            long id = Long.parseLong(idString);

            // Альтернативный способ получить параметр (если уверены, что он один)
            // Этот метод возвращает null, если параметра нет
            String idString2 = request.getParameter("id");

            // Ищем автомобиль по ID в репозитории
            Car car = carRepository.getById(id);

            // Проверяем, нашли ли автомобиль
            if (car == null) {

                // Автомобиль не найден → отправляем статус 404
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);  // 404

                // И сообщение об ошибке в JSON
                response.getWriter().write("{\"Сообщение\" : \"Модель автомобиля не найдена\"}");
            } else {

                // Автомобиль найден → преобразуем его в JSON
                String json = mapper.writeValueAsString(car);
                // Отправляем JSON клиенту
                response.getWriter().write(json);
            }
        }
    }

    // ============================================================
    // Метод для обработки HTTP POST запросов (создание нового авто)
    // ============================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // POST запрос используется для создания новых ресурсов
        // Пример: POST http://10.2.3.4:8080/cars

        // Получаем данные об автомобиле из тела запроса (в формате JSON)
        // req.getReader() получает поток для чтения тела запроса
        // mapper.readValue() преобразует JSON из запроса в объект model.Car
        Car car = mapper.readValue(req.getReader(), Car.class);

        // Сохраняем автомобиль в репозитории
        // Метод save обычно возвращает сохраненный объект с присвоенным ID
        car = carRepository.save(car);

        // Преобразуем сохраненный автомобиль обратно в JSON
        // И отправляем его в ответ (обычно с присвоенным ID)
        resp.getWriter().write(mapper.writeValueAsString(car));
    }


    // ============================================================
    // Метод PUT - для обновления
    // ============================================================

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Достаем данные из джейсон для обновления авто
        // Изменяем цену авто
//        Car dto = mapper.readValue(request.getReader(), Car.class);
//
//        //обновляем данные в нашем хранилище
//        Car updateCar = carRepository.update(dto);
//
//        if(updateCar==null){
//            // Если не удалось найти авто то возвращаем строку
//            response.getStatus();
//            response.getWriter().write("{\"message\" : \"Car not found\"}");
//        } else {
//            // При удачном обновлении авто мы его возвращаем в ответе
//            String json = mapper.writeValueAsString(updateCar);
//            response.getWriter().write(json);
//        }

        // Перед началом работ нам следует подготовить формат для ответа
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Для того чтобы обновить данные о машине нам нужно получить id объекта в котором
        // будут происходить именения, и тот параметр который следует изменить

        try {
            // Из запроса мы Получаем идентификатор в формате строки
            String idParam = request.getParameter("id");


            // Если такого идентификатора не существует то выбрасываем исключение
            // и останавливаем программу
            if (idParam == null){
                throw new IllegalArgumentException("На вход сервлета пришел null вместо ID");
            }

            // Если идентификатор существует то преобразуем его в параметр лонг
            Long id = Long.parseLong(idParam);

            // ObjectMapper mapper - это "переводчик" между JSON и Java-объектами.
            // req.getReader() - получаем данные от клиента
            // Car.class - указываем, во что преобразовать
            // mapper.readValue() - выполняем преобразование
            // Результат присваиваем переменной Car updatedCar
            Car updatedCar = mapper.readValue(request.getReader(), Car.class);
            // mapper смотрит на JSON и Class<Car>
            // и делает следующее:
            // 1. Читает JSON: {"brand":"Tesla","model":"Model 3","color":"Red","year":2023}
            // 2. Создает новый объект: Car car = new Car();
            // 3. Находит поле "brand" в JSON
            // 4. Ищет метод setBrand() в классе Car
            // 5. Вызывает: car.setBrand("Tesla");
            // 6. Повторяет для всех полей...

            // Эквивалентно ручному созданию:
            // Car updatedCar = new Car();
            // updatedCar.setBrand("BMW");
            // updatedCar.setModel("X5");
            // updatedCar.setColor("Black");
            // updatedCar.setYear(2022);
            // Но автоматически

            // Устанавливаем ID из параметра
            updatedCar.setId(id);

            // Сохраняем объект в репозиторий который в свою очередь уже сохранит ее в базу данных
            Car updateCar = carRepository.save(updatedCar);
            // Преобразуем сохраненный объект в JSON
            String json = mapper.writeValueAsString(updateCar);
            // отправляем клиенту
            response.getWriter().write(json);

        } catch (Exception e) {
            //Если мы попадаем в исключение то в ответе отправляем сообщение об ошибке
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


    // ============================================================
    // Метод DELETE (удаление)
    // ============================================================
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Задаем тип ответа и кодировку
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");


        try{
            // Получаем id из параметров запроса
            String idParam = request.getParameter("id");

            if(idParam == null){
                throw new IllegalArgumentException("id не может быть null");
            }

            // Преобразовываем строку в число
            Long id = Long.parseLong(idParam);

            // Удаляем объект из "Базы данных"
            Car result = carRepository.delete(id);

            // Отправляем клиенту объект который был удален
            response.getWriter().write(mapper.writeValueAsString(result));

        }catch (Exception e){
            // Сообщение которые отправятся клиенту в случае ошибки
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");

        }

    }
}