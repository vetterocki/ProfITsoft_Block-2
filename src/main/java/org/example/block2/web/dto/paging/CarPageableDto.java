package org.example.block2.web.dto.paging;

import java.time.Year;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CarPageableDto {
  private Long id;

  private String model;

  private Year yearManufactured;

  private String color;
}
