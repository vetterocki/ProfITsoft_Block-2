package org.example.block2.exception;

public class CarManufacturerNotFoundException extends RuntimeException {
  public CarManufacturerNotFoundException(Long canManufacturerId) {
    super(String.format("Car manufacturer with ID %d not found", canManufacturerId));
  }
}
