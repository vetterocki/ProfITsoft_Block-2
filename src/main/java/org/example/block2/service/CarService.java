package org.example.block2.service;

import java.time.Year;
import java.util.Optional;
import org.example.block2.domain.Car;
import org.example.block2.web.dto.UploadStatistics;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface CarService {
  Optional<Car> findById(Long id);

  Car create(Car car);

  Car update(Car car, Long id);

  void deleteById(Long id);

  Page<Car> findAllFilteredWithPagination(int page, int size, Long carManufacturerId, Year year);

  StreamingResponseBody findAllFilteredAsCsv(Long carManufacturerId, Year year);

  boolean existsByModel(String model);

  UploadStatistics uploadJson(MultipartFile file);
}
