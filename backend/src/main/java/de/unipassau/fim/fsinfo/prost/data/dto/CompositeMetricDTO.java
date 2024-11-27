package de.unipassau.fim.fsinfo.prost.data.dto;

import java.math.BigDecimal;

public record CompositeMetricDTO(String key1, String key1DisplayName, String key2,
                                 String key2DisplayName, BigDecimal value) {

}
