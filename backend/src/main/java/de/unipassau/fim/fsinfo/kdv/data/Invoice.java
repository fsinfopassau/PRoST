package de.unipassau.fim.fsinfo.kdv.data;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Invoice {

  private String userId;
  private Double price;
  private Map<String, Integer> amounts;

}
