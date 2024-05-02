package org.example.block2.web.dto.paging;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CarListRequest extends FilteredColumnsRequest {

  @Min(value = 1)
  private int page;

  @Min(value = 1)
  private int size;
}
