package org.example.block2.web.dto.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Year;
import lombok.Data;

@Data
public class CarManufacturerModifyDto {
  @NotBlank
  private String name;

  @NotBlank
  private String country;

  @NotNull
  private Year yearFounded;

  @NotNull
  private String CEO;
}
