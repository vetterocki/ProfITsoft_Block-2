package org.example.block2.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.example.block2.container.TestContainerConfiguration;
import org.example.block2.data.CarManufacturerRepository;
import org.example.block2.data.CarRepository;
import org.example.block2.web.dto.paging.CarListResponse;
import org.example.block2.web.dto.paging.CarPageableDto;
import org.example.block2.web.dto.paging.FilteredColumnsRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Import(TestContainerConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CarControllerIntegrationTest {
  private static final String URL = "cars";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CarManufacturerRepository carManufacturerRepository;

  @Autowired
  private CarRepository carRepository;

  @Autowired
  private ObjectMapper objectMapper;


  @AfterEach
  public void clearRepositoriesAfterEachTest() {
    carManufacturerRepository.deleteAll();
    carRepository.deleteAll();
  }

  private String buildCarJson(String model, int yearManufactured, String color,
                              Long carManufacturerId, String carTypes) {
    return String.format("""
        {
            "model": "%s",
            "yearManufactured": %d,
            "color": "%s",
            "carManufacturerId": %d,
            "carTypes": %s
        }
        """, model, yearManufactured, color, carManufacturerId, carTypes);
  }


  @Test
  @Sql("/sql/car_manufacturers.sql")
  void testCreationWithoutConstraintsViolation() throws Exception {
    String model = "Porsche Panamera";
    int yearManufactured = 2003;
    String color = "Yellow";
    Long carManufacturerId = 200L;
    String carTypes = "[\"SPORT\", \"COUPE\"]";

    String json = buildCarJson(model, yearManufactured, color, carManufacturerId, carTypes);

    mockMvc.perform(post("/{url}", URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated())
        .andExpectAll(
            jsonPath("$.id", notNullValue()),
            jsonPath("$.model", is(model)),
            jsonPath("$.yearManufactured", is(yearManufactured)),
            jsonPath("$.color", is(color)),
            jsonPath("$.carManufacturer.id", is(carManufacturerId.intValue())),
            jsonPath("$.carTypes", hasSize(2))
        );
  }

  @Test
  @Sql({"/sql/car_manufacturers.sql", "/sql/cars.sql"})
  void testCreationWithConstraintsViolation() throws Exception {
    String model = "Nissan";
    int yearManufactured = 2003;
    String color = "Yellow";
    Long carManufacturerId = 200L;
    String carTypes = "[\"SPORT\", \"COUPE\"]";

    String json = buildCarJson(model, yearManufactured, color, carManufacturerId, carTypes);

    mockMvc.perform(post("/{url}", URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Sql({"/sql/car_manufacturers.sql", "/sql/cars.sql"})
  void testUpdatingWithoutConstraintsViolation() throws Exception {
    Long carId = 200L;
    Long manufacturerId = 300L;
    String updated = "Updated";
    int newYearManufactured = 2020;
    String carTypes = "[\"SPORT\", \"COUPE\"]";

    String json = buildCarJson(updated, newYearManufactured, updated, manufacturerId, carTypes);

    mockMvc.perform(put("/{url}/{id}", URL, carId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk())
        .andExpectAll(
            jsonPath("$.id", notNullValue()),
            jsonPath("$.model", is(updated)),
            jsonPath("$.yearManufactured", is(newYearManufactured)),
            jsonPath("$.color", is(updated)),
            jsonPath("$.carManufacturer.id", is(manufacturerId.intValue())),
            jsonPath("$.carTypes", hasSize(2))
        );
  }

  @Test
  @Sql({"/sql/car_manufacturers.sql", "/sql/cars.sql"})
  void testUpdatingWithConstraintsViolation() throws Exception {
    Long carId = 200L;
    Long manufacturerId = 300L;
    String updated = "Nissan";
    int newYearManufactured = 2020;
    String carTypes = "[\"SPORT\", \"COUPE\"]";

    String json = buildCarJson(updated, newYearManufactured, updated, manufacturerId, carTypes);

    mockMvc.perform(put("/{url}/{id}", URL, carId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Sql({"/sql/car_manufacturers.sql", "/sql/cars.sql"})
  void testFindingById() throws Exception {
    Long id = 200L;
    mockMvc.perform(get("/{url}/{id}", URL, id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(id.intValue())));
  }


  @Test
  @Sql({"/sql/car_manufacturers.sql", "/sql/cars.sql"})
  void testDeletion() throws Exception {
    Long carId = 200L;
    mockMvc.perform(delete("/{url}/{id}", URL, carId))
        .andExpect(status().isNoContent());
    assertFalse(carRepository.existsById(carId));
  }

  @ParameterizedTest
  @MethodSource("paginationTestData")
  @Sql({"/sql/car_manufacturers.sql", "/sql/cars.sql"})
  void testFindingAllFilteredWithPagination(CarListResponse response, String request)
      throws Exception {
    mockMvc.perform(post("/{url}/_list", URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list", hasSize(response.getList().size())))
        .andExpect(jsonPath("$.totalPages", is(response.getTotalPages())));
  }

  @ParameterizedTest
  @MethodSource("csvTestData")
  @Sql({"/sql/car_manufacturers.sql", "/sql/cars.sql"})
  void testFindingAllFilteredAsCsv(FilteredColumnsRequest request, String csv) throws Exception {
    mockMvc.perform(post("/{url}/_report", URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(request().asyncStarted())
        .andDo(MvcResult::getAsyncResult)
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Type", "text/csv;charset=UTF-8"))
        .andExpect(header().string("Content-Disposition", "attachment; filename=cars.csv"))
        .andExpect(content().string(csv));
  }

  @Test
  @Sql("/sql/car_manufacturers.sql")
  void testUploadingJsonFile() throws Exception {
    MockMultipartFile json = new MockMultipartFile("file", "cars-test.json", "application/json",
        Files.newInputStream(Paths.get("src/test/resources/cars-test.json")));

    mockMvc.perform(multipart("/{url}/upload", URL)
            .file(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.succeedAmount", is(2)))
        .andExpect(jsonPath("$.failedAmount", is(3)));
  }

  private static Stream<Arguments> paginationTestData() {
    var firstCar = new CarPageableDto(200L, "Porsche", Year.of(2010), "Red");
    var secondCar = new CarPageableDto(300L, "Nissan", Year.of(2015), "Yellow");

    return Stream.of(
        Arguments.of(new CarListResponse(List.of(firstCar), 1),
            """
                  {
                      "carManufacturerId": 200,
                      "yearManufactured": 2010,
                      "page": 1,
                      "size": 2
                  }
                """),
        Arguments.of(new CarListResponse(List.of(secondCar), 2),
            """
                  {
                      "page": 2,
                      "size": 1
                  }
                """),
        Arguments.of(new CarListResponse(Collections.emptyList(), 0),
            """
                 {
                     "carManufacturerId": 1,
                     "page": 1,
                     "size": 2
                 }
                """)
    );
  }

  private static Stream<Arguments> csvTestData() {
    return Stream.of(
        Arguments.of(new FilteredColumnsRequest(200L, Year.of(2010)),
            """
                "color","id","model","year_manufactured"
                "Red","200","Porsche","2010"
                """),
        Arguments.of(new FilteredColumnsRequest(null, null),
            """
                "color","id","model","year_manufactured"
                "Red","200","Porsche","2010"
                "Yellow","300","Nissan","2015"
                """),
        Arguments.of(new FilteredColumnsRequest(1L, null), "")
    );
  }


}
