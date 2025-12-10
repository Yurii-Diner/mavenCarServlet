package repository;

import model.Car;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarRepositoryMap implements CarRepository {
    private Map<Long, Car> storage = new HashMap<>();
    private long currentId;

    public CarRepositoryMap() {
        initStorage();
    }

    private void initStorage() {
        save(new Car("Mazda", 2018, new BigDecimal(12000)));
        save(new Car("VW", 2021, new BigDecimal(15000)));
        save(new Car("Ford", 2022, new BigDecimal(14000)));
        save(new Car("BMW", 2023, new BigDecimal(35000)));
        save(new Car("Opel", 2024, new BigDecimal(21000)));
    }


    @Override
    public List<Car > getAll() {
        return storage.values().stream().toList();
    }

    @Override
    public Car save(Car car) {
        // создаем новый айди
        car.setId(++currentId);

        //
        storage.put(car.getId(), car);
        return car;
    }

    @Override
    public Car getById(long id) {
        return storage.getOrDefault(id, null);
    }

//    @Override
//    public Car update(Car car) {
//
//        // Проверка входных данных
//        if (car.getId() == null) {
//            throw new IllegalArgumentException("ID не может быть null");
//        }
//
//        // Проверяем наличие такого id в нашем хранилище
//        if (!storage.containsKey(car.getId())) {
//            // Если его там нет, то вместо объекта машины возвращаем null
//            return null;
//        }
//
//        // Если все проверки пройдены, и такой id есть в нашей базе, то обращаемся
//        // К этому ключу и кладем новый объект машины
//        storage.put(car.getId(), car);
//
//
//        return car;
//    }

    @Override
    public Car update(Car car) {
        Long id = car.getId();
        BigDecimal newPrice = car.getPrice();

        // Добавить проверки

        Car carToUpdate = storage.getOrDefault(id, null);
        if (carToUpdate == null) return null;

        carToUpdate.setPrice(newPrice);
        return carToUpdate;
    }


    @Override
    public Car delete(long id) {
        return storage.remove(id);
    }
}
