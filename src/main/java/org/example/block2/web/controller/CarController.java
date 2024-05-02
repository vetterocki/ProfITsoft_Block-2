package org.example.block2.web.controller;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import jakarta.validation.Valid;
import java.time.Year;
import lombok.RequiredArgsConstructor;
import org.example.block2.domain.Car;
import org.example.block2.service.CarService;
import org.example.block2.web.dto.domain.CarModifyDto;
import org.example.block2.web.dto.domain.CarViewDto;
import org.example.block2.web.dto.paging.CarListRequest;
import org.example.block2.web.dto.paging.CarListResponse;
import org.example.block2.web.dto.paging.FilteredColumnsRequest;
import org.example.block2.web.dto.UploadStatistics;
import org.example.block2.web.mapper.CarMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RequiredArgsConstructor
@RequestMapping("/cars")
@RestController
public class CarController {
  private final CarService carService;
  private final CarMapper carMapper;

  @GetMapping("/{id}")
  public ResponseEntity<CarViewDto> findById(@PathVariable Long id) {
    return ResponseEntity.of(carService.findById(id).map(carMapper::toDto));
  }

  @PostMapping
  public ResponseEntity<CarViewDto> create(@Valid @RequestBody CarModifyDto carModifyDto) {
    Car created = carService.create(carMapper.toEntity(carModifyDto));
    return ResponseEntity.status(HttpStatus.CREATED).body(carMapper.toDto(created));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CarViewDto> update(@Valid @RequestBody CarModifyDto carModifyDto,
                                           @PathVariable Long id) {
    Car updated = carService.update(carMapper.toEntity(carModifyDto), id);
    return ResponseEntity.ok(carMapper.toDto(updated));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    carService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/_list")
  public ResponseEntity<CarListResponse> findAllFilteredWithPagination(@Valid @RequestBody
                                                                         CarListRequest request) {
    Page<Car> page = carService.findAllFilteredWithPagination(
        request.getPage(), request.getSize(), request.getCarManufacturerId(), request.getYearManufactured()
    );
    CarListResponse response = CarListResponse.fromPage(page.map(carMapper::toPageableDto));
    return ResponseEntity.ok(response);
  }

  @PostMapping("/_report")
  public ResponseEntity<StreamingResponseBody> findAllFilteredAsCsv(@Valid @RequestBody
                                                              FilteredColumnsRequest request) {
    Long manufacturerId = request.getCarManufacturerId();
    Year year = request.getYearManufactured();
    StreamingResponseBody stream = carService.findAllFilteredAsCsv(manufacturerId, year);
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
        .header(CONTENT_DISPOSITION, String.format("attachment; filename=%s", "cars.csv"))
        .header(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION)
        .body(stream);
  }

  @PostMapping("/upload")
  public ResponseEntity<UploadStatistics> upload(@RequestParam("file") MultipartFile file) {
    UploadStatistics uploadStatistics = carService.uploadJson(file);
    return ResponseEntity.ok().body(uploadStatistics);
  }

}
