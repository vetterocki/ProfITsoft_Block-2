package org.example.block2.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ExceptionResponse {
  private String message;

  private String timeStamp;
}
