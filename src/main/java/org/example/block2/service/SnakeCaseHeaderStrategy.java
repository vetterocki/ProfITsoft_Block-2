package org.example.block2.service;

import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * A custom mapping strategy for StatefulBeanToCsv class,
 * which converts CamelCase headers to snake_case and sorts them alphabetically.
 * Replaces default MappingStrategy, which does not apply sorting and generate UPPER_CASE header.
 *
 * @param <T> the type of the bean class mapped by this strategy
 */
public class SnakeCaseHeaderStrategy<T> extends HeaderColumnNameTranslateMappingStrategy<T> {
  public SnakeCaseHeaderStrategy(Class<? extends T> clazz) {
    Map<String, String> fieldMap = Arrays.stream(clazz.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toMap(String::toUpperCase, Function.identity()));
    super.setType(clazz);
    super.setColumnMapping(fieldMap);
    super.setColumnOrderOnWrite(Comparator.naturalOrder());
  }

  @Override
  public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
    var columnMapping = super.getColumnMapping();
    return Arrays.stream(super.generateHeader(bean))
        .map(columnMapping::get)
        .map(column -> column.replaceAll("([A-Z])", "_$1"))
        .map(String::toLowerCase)
        .toArray(String[]::new);

  }
}
