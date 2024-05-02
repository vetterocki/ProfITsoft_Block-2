package org.example.block2.web.dto.paging;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CarListResponse {
  private List<CarPageableDto> list;

  private int totalPages;

  public static CarListResponse fromPage(Page<CarPageableDto> dtos) {
    CarListResponse response = new CarListResponse();
    response.setTotalPages(dtos.getTotalPages());
    response.setList(dtos.getContent());
    return response;
  }
}
