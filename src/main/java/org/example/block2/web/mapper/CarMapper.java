package org.example.block2.web.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.example.block2.domain.Car;
import org.example.block2.web.dto.domain.CarModifyDto;
import org.example.block2.web.dto.domain.CarViewDto;
import org.example.block2.web.dto.paging.CarPageableDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = CarManufacturerMapper.class)
public interface CarMapper {

  @Mapping(target = "carManufacturer.id", source = "carManufacturerId")
  @Mapping(target = "carTypes", source = "carTypes")
  Car toEntity(CarModifyDto carModifyDto);

  @InheritInverseConfiguration(name = "toEntity")
  CarViewDto toDto(Car car);

  CarPageableDto toPageableDto(Car car);

  default Set<String> mapCarTypes(Set<Car.CarType> carTypes) {
    return carTypes.stream()
        .map(Car.CarType::getTypeName)
        .collect(Collectors.toSet());
  }

  default Set<Car.CarType> mapCarTypeStrings(Set<String> carTypeStrings) {
    return carTypeStrings.stream()
        .map(Car.CarType::new)
        .collect(Collectors.toSet());
  }
}


