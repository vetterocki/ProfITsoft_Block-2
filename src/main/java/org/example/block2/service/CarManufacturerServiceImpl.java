package org.example.block2.service;

import java.util.List;
import java.util.Optional;
import org.example.block2.data.CarManufacturerRepository;
import org.example.block2.domain.CarManufacturer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarManufacturerServiceImpl implements CarManufacturerService {
  private final CarManufacturerRepository carManufacturerRepository;
  private final CarManufacturerServiceImpl self;

  public CarManufacturerServiceImpl(CarManufacturerRepository carManufacturerRepository,
                                    @Lazy CarManufacturerServiceImpl self) {
    this.carManufacturerRepository = carManufacturerRepository;
    this.self = self;
  }

  @Override
  public Optional<CarManufacturer> findById(Long id) {
    return carManufacturerRepository.findById(id);
  }

  @Override
  @Transactional
  public CarManufacturer create(CarManufacturer carManufacturer) {
    return carManufacturerRepository.save(carManufacturer);
  }

  @Override
  @Transactional
  public CarManufacturer update(CarManufacturer carManufacturer, Long id) {
    CarManufacturer updated = findById(id)
        .map(carManufacturerInDb -> carManufacturerInDb.toBuilder()
            .country(carManufacturer.getCountry())
            .CEO(carManufacturer.getCEO())
            .name(carManufacturer.getName())
            .yearFounded(carManufacturer.getYearFounded())
            .build())
        .orElseGet(() -> {
          carManufacturer.setId(id);
          return carManufacturer;
        });
    return self.create(updated);
  }

  @Override
  public List<CarManufacturer> findAll() {
    return carManufacturerRepository.findAll();
  }

  @Override
  public void deleteById(Long id) {
    carManufacturerRepository.deleteById(id);
  }

  @Override
  public boolean existsById(Long id) {
    return carManufacturerRepository.existsById(id);
  }
}
