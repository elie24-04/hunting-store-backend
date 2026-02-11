package com.fypbackend.spring_boot.dao;

import java.math.BigDecimal;

public interface SalesPeriodAggregation {
    String getPeriod();

    BigDecimal getTotal();
}
