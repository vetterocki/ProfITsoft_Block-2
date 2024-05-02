package org.example.block2.service;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import java.io.Writer;
import org.example.block2.domain.Car;
import org.springframework.stereotype.Component;

/**
 * Creator class responsible for creating a stateful CSV bean for Car objects with customized settings.
 * This class configures a CSV bean writer for Car objects, applying a custom header generation strategy
 * and excluding specific fields that are unnecessary for CSV output.
 */
@Component
public class StatefulCarBeanToCsvCreator {

  public StatefulBeanToCsv<Car> createCsvBean(Writer writer) throws NoSuchFieldException {
    SnakeCaseHeaderStrategy<Car> strategy = new SnakeCaseHeaderStrategy<>(Car.class);
    return new StatefulBeanToCsvBuilder<Car>(writer)
        .withMappingStrategy(strategy)
        .withIgnoreField(Car.class, Car.class.getDeclaredField("carManufacturer"))
        .withIgnoreField(Car.class, Car.class.getDeclaredField("carTypes"))
        .build();
  }

}
