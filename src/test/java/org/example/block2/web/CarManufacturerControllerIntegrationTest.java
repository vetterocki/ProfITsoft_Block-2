package org.example.block2.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import org.example.block2.container.TestContainerConfiguration;
import org.example.block2.data.CarManufacturerRepository;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

@Import(TestContainerConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CarManufacturerControllerIntegrationTest {
  private static final String URL = "manufacturers";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CarManufacturerRepository carManufacturerRepository;

  @AfterEach
  public void clearRepository() {
    carManufacturerRepository.deleteAll();
  }

  private String buildManufacturerJson(String name, String country, int yearFounded, String ceo) {
    return String.format("""
        {
            "name": "%s",
            "country": "%s",
            "yearFounded": %d,
            "ceo": "%s"
        }
        """, name, country, yearFounded, ceo);
  }


  @Test
  void testCreationWithoutConstraintsViolation() throws Exception {
    String companyName = "Tesla";
    String country = "USA";
    int yearFounded = 2003;
    String ceo = "Elon Musk";

    String json = buildManufacturerJson(companyName, country, yearFounded, ceo);

    mockMvc.perform(post("/{url}", URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated())
        .andExpectAll(
            jsonPath("$.id", notNullValue()),
            jsonPath("$.name", is(companyName)),
            jsonPath("$.yearFounded", is(yearFounded)),
            jsonPath("$.country", is(country)),
            jsonPath("$.ceo", is(ceo))
        );
  }

  @Test
  @Sql("/sql/car_manufacturers.sql")
  void testCreationWithConstraintsViolation() throws Exception {
    String companyName = "Test company";
    String country = "USA";
    int yearFounded = 2003;
    String ceo = "Elon Musk";

    String json = buildManufacturerJson(companyName, country, yearFounded, ceo);

    mockMvc.perform(post("/{url}", URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Sql("/sql/car_manufacturers.sql")
  void testUpdateWithoutConstraintsViolation() throws Exception {
    Long manufacturerIdInDb = 200L;
    String updated = "Updated";
    int changedYear = 2004;

    String json = buildManufacturerJson(updated, updated, changedYear, updated);

    mockMvc.perform(put("/{url}/{id}", URL, manufacturerIdInDb)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk())
        .andExpectAll(
            jsonPath("$.id", notNullValue()),
            jsonPath("$.name", is(updated)),
            jsonPath("$.yearFounded", is(changedYear)),
            jsonPath("$.country", is(updated)),
            jsonPath("$.ceo", is(updated))
        );
  }

  @Test
  @Sql("/sql/car_manufacturers.sql")
  void testUpdateWithConstraintsViolation() throws Exception {
    Long manufacturerIdInDb = 300L;
    String updated = "Test company";
    int changedYear = 2004;

    String json = buildManufacturerJson(updated, updated, changedYear, updated);

    mockMvc.perform(put("/{url}/{id}", URL, manufacturerIdInDb)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Sql("/sql/car_manufacturers.sql")
  void testFindingAll() throws Exception {
    mockMvc.perform(get("/{url}", URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @ParameterizedTest
  @Sql("/sql/car_manufacturers.sql")
  @MethodSource("deleteTestData")
  void testDeletion(Long id, ResultMatcher expectedStatus) throws Exception {
    mockMvc.perform(delete("/{url}/{id}", URL, id)).andExpect(expectedStatus);
    assertFalse(carManufacturerRepository.existsById(id));
  }

  private static Stream<Arguments> deleteTestData() {
    return Stream.of(
        Arguments.of(200L, status().isNoContent()),
        Arguments.of(300L, status().isNoContent())
    );
  }
}
