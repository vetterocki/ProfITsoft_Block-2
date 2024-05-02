package org.example.block2.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UploadStatistics {
  private Long succeedAmount;

  private Long failedAmount;

  public static UploadStatistics empty() {
    return new UploadStatistics(0L, 0L);
  }
}
