package repository;

import jakarta.persistence.EntityManager;
import model.Car;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class CarRepositoryHibernate implements CarRepository {

    private EntityManager entityManager;

    public CarRepositoryHibernate() {

        entityManager = new Configuration()
                .configure("hibernate/postgres.cfg.xml")
                .buildSessionFactory()
                .createEntityManager();
    }


    @Override
    public List<Car> getAll() {
        return entityManager.createQuery("from Car", Car.class).getResultList();
    }

    @Override
    public Car save(Car car) {
        if (car == null) {
            return null;
        }
        entityManager.getTransaction().begin();
        entityManager.persist(car);
        entityManager.getTransaction().commit();

        return car;
    }

    @Override
    public Car getById(long id) {
        return entityManager.find(Car.class, id);
    }

    @Override
    public Car update(Car car) {
        Car updatedCar = entityManager.find(Car.class, car.getId());
        if(updatedCar == null) {
            return null;
        }
        entityManager.getTransaction().begin();
        entityManager.merge(car);
        entityManager.getTransaction().commit();

        return updatedCar;
    }

    @Override
    public Car delete(long id) {
        Car carToDelete = entityManager.find(Car.class, id);
        if(carToDelete == null) {
            return null;
        }
        entityManager.getTransaction().begin();
        entityManager.remove(carToDelete);
        entityManager.getTransaction().commit();

        return carToDelete;
    }
}
