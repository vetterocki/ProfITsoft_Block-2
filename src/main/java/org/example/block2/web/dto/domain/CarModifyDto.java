package org.example.block2.web.dto.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Year;
import java.util.Set;
import lombok.Data;

@Data
public class CarModifyDto {
  @NotBlank
  private String model;

  @NotNull
  private Long carManufacturerId;

  @NotNull
  private Year yearManufactured;

  @NotBlank
  private String color;

  @NotNull
  private Set<String> carTypes;
}
