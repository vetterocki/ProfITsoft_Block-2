package org.example.block2.service;

import java.util.List;
import java.util.Optional;
import org.example.block2.domain.CarManufacturer;

public interface CarManufacturerService {
  Optional<CarManufacturer> findById(Long id);

  CarManufacturer create(CarManufacturer carManufacturer);

  CarManufacturer update(CarManufacturer carManufacturer, Long id);

  List<CarManufacturer> findAll();

  void deleteById(Long id);

  boolean existsById(Long id);
}
