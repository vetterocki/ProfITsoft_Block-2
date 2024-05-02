package org.example.block2.web.dto.paging;

import java.time.Year;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FilteredColumnsRequest {
  private Long carManufacturerId;

  private Year yearManufactured;
}
