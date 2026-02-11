package com.fypbackend.spring_boot.dao;

import java.math.BigDecimal;

public interface TopSellingProductProjection {
    Long getProductId();

    String getProductName();

    Long getUnitsSold();

    BigDecimal getRevenue();
}
