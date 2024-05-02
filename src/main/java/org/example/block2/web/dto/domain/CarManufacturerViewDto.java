package org.example.block2.web.dto.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CarManufacturerViewDto extends CarManufacturerModifyDto {
  private Long id;
}
