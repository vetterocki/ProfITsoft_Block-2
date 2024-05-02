package org.example.block2.web.mapper;

import org.example.block2.domain.CarManufacturer;
import org.example.block2.web.dto.domain.CarManufacturerModifyDto;
import org.example.block2.web.dto.domain.CarManufacturerViewDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarManufacturerMapper {
  CarManufacturer toEntity(CarManufacturerModifyDto carManufacturerModifyDto);

  CarManufacturerViewDto toDto(CarManufacturer carManufacturer);
}
