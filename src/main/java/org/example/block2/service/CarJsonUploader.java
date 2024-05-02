package org.example.block2.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.block2.web.dto.domain.CarModifyDto;
import org.example.block2.web.dto.UploadStatistics;
import org.example.block2.web.mapper.CarMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class CarJsonUploader implements JsonUploader {
  private final CarMapper carMapper;
  private final CarService carService;
  private final CarManufacturerService carManufacturerService;
  private final Validator validator;
  private final ObjectMapper objectMapper;

  private final AtomicLong successfulUploads = new AtomicLong(0);
  private final AtomicLong failedUploads = new AtomicLong(0);

  /**
   * Uploads and processes a JSON file containing an array of objects representing car modifications.
   * Each object is sequentially read from the JSON input stream and validated before saving to the database.
   * Validation includes checking constraints on object fields and ensuring uniqueness of the 'model' field.
   * Additionally, the 'carManufacturerId' field is validated to ensure the manufacturer exists in the database.
   *
   * @param json the multipart file containing JSON data to upload
   * @return UploadStatistics object representing the number of successful and failed uploads
   */

  @Override
  public UploadStatistics uploadJson(MultipartFile json) {

    try (BufferedReader br = new BufferedReader(new InputStreamReader(json.getInputStream()));
         JsonParser jsonParser = objectMapper.getFactory().createParser(br)) {

      if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
        throw new IllegalStateException("Expected content to be an array");
      }


      while (jsonParser.nextToken() == JsonToken.START_OBJECT) {
        JsonNode root = objectMapper.readTree(jsonParser);
        CarModifyDto carModifyDto = objectMapper.treeToValue(root, CarModifyDto.class);
        saveValidCar(carModifyDto);
      }

      return new UploadStatistics(successfulUploads.get(), failedUploads.get());

    } catch (IOException ioException) {
      log.warn(String.format("Error while uploading json: %s", ioException.getMessage()));
    }

    return UploadStatistics.empty();
  }

  /**
   * Validates and saves a car modification DTO if it passes validation checks.
   *
   * @param carModifyDto the CarModifyDto object representing a car modification to be validated and saved
   */

  private void saveValidCar(CarModifyDto carModifyDto) {
    Optional.ofNullable(carModifyDto)
        .filter(this::verifyCarModifyDto)
        .map(carMapper::toEntity)
        .map(carService::create)
        .ifPresentOrElse(
            created -> successfulUploads.getAndIncrement(),
            failedUploads::getAndIncrement
        );
  }

  /**
   * Performs car DTO validation for 3 requirements:
   * 1) Fields of objects are valid due to jakarta constraints
   * 2) Field 'model' is unique due to database index
   * 3) Manufacturer exists by id which is specified in 'carManufacturerId' field
   *
   * @param dto, which represents single object from JSON array
   * @return boolean representing all validation checks passed.
   */

  private boolean verifyCarModifyDto(CarModifyDto dto) {
    String model = dto.getModel();
    Long id = dto.getCarManufacturerId();
    return isValidCarModifyDto(dto) && doesNotModelExist(model) && doesManufacturerExist(id);
  }

  private boolean isValidCarModifyDto(CarModifyDto dto) {
    return validator.validate(dto).isEmpty();
  }

  private boolean doesNotModelExist(String model) {
    return !carService.existsByModel(model);
  }

  private boolean doesManufacturerExist(Long manufacturerId) {
    return carManufacturerService.existsById(manufacturerId);
  }




}
