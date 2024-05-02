package org.example.block2.service;


import static java.nio.charset.StandardCharsets.UTF_8;
import static org.example.block2.data.CarSpecifications.dynamicWhere;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.block2.data.CarRepository;
import org.example.block2.domain.Car;
import org.example.block2.exception.CarManufacturerNotFoundException;
import org.example.block2.web.dto.UploadStatistics;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@Slf4j
@Service
public class CarServiceImpl implements CarService {

  private final CarRepository carRepository;
  private final CarManufacturerService carManufacturerService;
  private final StatefulCarBeanToCsvCreator statefulCarBeanToCsvCreator;
  private final JsonUploader jsonUploader;
  private final CarServiceImpl self;

  @Override
  public Optional<Car> findById(Long id) {
    return carRepository.findById(id);
  }

  @Override
  @Transactional
  public Car create(Car car) {
    Long carManufacturerId = car.getCarManufacturer().getId();
    return carManufacturerService.findById(carManufacturerId)
        .map(car::withCarManufacturer)
        .map(carRepository::save)
        .orElseThrow(() -> new CarManufacturerNotFoundException(carManufacturerId));
  }

  @Override
  @Transactional
  public Car update(Car car, Long id) {
    Car updated = findById(id)
        .map(carInDb -> carInDb.toBuilder()
            .model(car.getModel())
            .carTypes(car.getCarTypes())
            .carManufacturer(car.getCarManufacturer())
            .color(car.getColor())
            .yearManufactured(car.getYearManufactured())
            .build())
        .orElseGet(() -> {
          car.setId(id);
          return car;
        });
    return self.create(updated);
  }

  @Override
  public Page<Car> findAllFilteredWithPagination(int page, int size, Long carManufacturerId,
                                                 Year year) {
    Pageable pageable = PageRequest.of(page - 1, size);
    return carRepository.findAll(dynamicWhere(carManufacturerId, year), pageable);
  }

  @Override
  public StreamingResponseBody findAllFilteredAsCsv(Long carManufacturerId, Year year) {
    return outputStream -> {
      try (Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8))) {
        StatefulBeanToCsv<Car> toCsvBean = statefulCarBeanToCsvCreator.createCsvBean(writer);
        List<Car> cars = carRepository.findAll(dynamicWhere(carManufacturerId, year));

        toCsvBean.write(cars);

      } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException |
               NoSuchFieldException exception) {
        log.warn("Error while writing csv file {}", exception.getMessage());
        throw new RuntimeException(exception);
      }
    };
  }

  @Override
  public boolean existsByModel(String model) {
    return carRepository.existsByModel(model);
  }

  @Override
  public UploadStatistics uploadJson(MultipartFile file) {
    return jsonUploader.uploadJson(file);
  }

  @Override
  public void deleteById(Long id) {
    carRepository.deleteById(id);
  }
}
