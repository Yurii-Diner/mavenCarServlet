package model;

import java.math.BigDecimal;
import java.util.Objects;

public class Car {
    private Long id;
    private String brand;
    private int year;
    private BigDecimal price;

    public Car() {
    }

    public Car(String brand, int year, BigDecimal price) {
        this.brand = brand;
        this.year = year;
        this.price = price;
    }

    public Car(Long id, String brand, int year, BigDecimal price) {
        this.id = id;
        this.brand = brand;
        this.year = year;
        this.price = price;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public int getYear() {
        return year;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "model.Car{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", year=" + year +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Car car)) return false;
        return year == car.year && Objects.equals(id, car.id) && Objects.equals(brand, car.brand) && Objects.equals(price, car.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, brand, year, price);
    }
}
