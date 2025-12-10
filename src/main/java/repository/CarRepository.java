package repository;
import model.Car;

import java.util.List;

public interface CarRepository {
    List<Car> getAll();
    Car save(Car car);

    Car getById(long id);

    // Метод для обновления
    Car update(Car car);

    // Метод для удаления
    Car delete(long id);
    // boolean delete(Long id);
}
