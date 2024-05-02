package org.example.block2.web.controller;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.block2.domain.CarManufacturer;
import org.example.block2.service.CarManufacturerService;
import org.example.block2.web.dto.domain.CarManufacturerModifyDto;
import org.example.block2.web.dto.domain.CarManufacturerViewDto;
import org.example.block2.web.mapper.CarManufacturerMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/manufacturers")
@RestController
public class CarManufacturerController {
  private final CarManufacturerMapper carManufacturerMapper;
  private final CarManufacturerService carManufacturerService;

  @GetMapping
  public ResponseEntity<List<CarManufacturerViewDto>> findAll() {
    return carManufacturerService.findAll().stream()
        .map(carManufacturerMapper::toDto)
        .collect(collectingAndThen(toList(), ResponseEntity::ok));
  }

  @PostMapping
  public ResponseEntity<CarManufacturerViewDto> create(@Valid @RequestBody CarManufacturerModifyDto dto) {
    CarManufacturer created = carManufacturerService.create(carManufacturerMapper.toEntity(dto));
    return ResponseEntity.status(HttpStatus.CREATED).body(carManufacturerMapper.toDto(created));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CarManufacturerViewDto> update(@Valid @RequestBody CarManufacturerModifyDto dto,
                                                       @PathVariable Long id) {
    CarManufacturer updated = carManufacturerService.update(carManufacturerMapper.toEntity(dto), id);
    return ResponseEntity.ok(carManufacturerMapper.toDto(updated));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<CarManufacturerViewDto> deleteById(@PathVariable Long id) {
    carManufacturerService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

}
