package com.fypbackend.spring_boot.dao;

import java.math.BigDecimal;

public interface KpiProjection {
    BigDecimal getTotalSales();

    Long getTotalOrders();

    BigDecimal getAvgOrderValue();
}
