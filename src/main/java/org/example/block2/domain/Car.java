package org.example.block2.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Year;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import org.hibernate.proxy.HibernateProxy;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "cars", indexes = {
    @Index(name = "multi_sort_index", columnList = "car_manufacturer_id, year_manufactured"),
    @Index(name = "car_manufactured_id_index", columnList = "car_manufacturer_id"),
    @Index(name = "year_manufactured_index", columnList = "year_manufactured"),
    @Index(name = "unique_model_index", columnList = "model", unique = true),
})
public class Car {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String model;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "car_manufacturer_id")
  @With
  @ToString.Exclude
  private CarManufacturer carManufacturer;

  private Year yearManufactured;

  private String color;

  @ElementCollection
  @CollectionTable(name = "car_types", joinColumns = @JoinColumn(name = "car_id"))
  private Set<CarType> carTypes;

  @Data
  @NoArgsConstructor
  @Embeddable
  public static class CarType {
    private String typeName;

    public CarType(String typeName) {
      this.typeName = typeName;
    }
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    Class<?> oEffectiveClass = o instanceof HibernateProxy ?
        ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() :
        o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
        ((HibernateProxy) this).getHibernateLazyInitializer()
            .getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) {
      return false;
    }
    Car car = (Car) o;
    return getId() != null && Objects.equals(getId(), car.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ?
        ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
        getClass().hashCode();
  }
}
