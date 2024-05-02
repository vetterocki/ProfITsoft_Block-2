package org.example.block2.data;

import static org.springframework.data.jpa.domain.Specification.where;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.time.Year;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.example.block2.domain.Car;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class CarSpecifications {

  /**
   * Builds a predicate to be used in creating Specifications (Spring Data JPA) based on the provided attribute value.
   * If the attribute is not null, this method constructs a Predicate representing an equality condition for the attribute.
   *
   * <p>
   * The Specification interface in Spring Data JPA encapsulates a single predicate that can be used to construct
   * complex queries. This method assists in building such predicates dynamically based on the attribute value.
   * </p>
   *
   * @param <A>       The type of the attribute
   * @param attribute The attribute value (may be null)
   * @param cb        The CriteriaBuilder instance used to construct criteria queries
   * @param path      The Path representing the attribute in the query
   * @return A Predicate representing the equality condition for the attribute if it is not null; otherwise, null
   */
  private static <A> Predicate buildPredicate(A attribute, CriteriaBuilder cb, Path<A> path) {
    return Optional.ofNullable(attribute).map(value -> cb.equal(path, value)).orElse(null);
  }


  public static Specification<Car> hasManufacturer(Long carManufacturerId) {
    return (car, query, criteriaBuilder) -> {
      Path<Long> pathToCarManufacturer = car.join("carManufacturer").get("id");
      return buildPredicate(carManufacturerId, criteriaBuilder, pathToCarManufacturer);
    };
  }


  public static Specification<Car> hasYearManufactured(Year yearManufactured) {
    return (car, query, criteriaBuilder) -> {
      Path<Year> pathToYear = car.get("yearManufactured");
      return buildPredicate(yearManufactured, criteriaBuilder, pathToYear);
    };
  }

  /**
   * Creates a Specification object representing a dynamic WHERE clause based on the provided request parameters
   * (if some parameter is NULL, it won`t be added to WHERE clause).
   * This method constructs a Specification that combines predicates based on the given carManufacturerId and year.
   *
   * @param carManufacturerId The ID of the car manufacturer to filter by (can be null)
   * @param year              The year of manufacture to filter by (can be null)
   * @return A Specification<Car> representing the dynamic WHERE clause based on the request parameters
   */
  public static Specification<Car> dynamicWhere(Long carManufacturerId, Year year) {
    return where(hasManufacturer(carManufacturerId)).and(hasYearManufactured(year));
  }

}
