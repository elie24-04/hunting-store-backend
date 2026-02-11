package com.fypbackend.spring_boot.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Pageable;

import com.fypbackend.spring_boot.dao.KpiProjection;
import com.fypbackend.spring_boot.dao.OrderCountPeriodAggregation;
import com.fypbackend.spring_boot.dao.OrderRepository;
import com.fypbackend.spring_boot.dao.ProductRepository;
import com.fypbackend.spring_boot.dao.SalesPeriodAggregation;
import com.fypbackend.spring_boot.dao.TopSellingProductProjection;
import com.fypbackend.spring_boot.dto.admin.InventoryAlertDto;
import com.fypbackend.spring_boot.dto.admin.InventoryAlertResponse;
import com.fypbackend.spring_boot.dto.admin.KpiResponse;
import com.fypbackend.spring_boot.dto.admin.OrderStatisticsResponse;
import com.fypbackend.spring_boot.dto.admin.SalesTrendResponse;
import com.fypbackend.spring_boot.dto.admin.TopSellingProductDto;
import com.fypbackend.spring_boot.dto.admin.TopSellingProductResponse;
import com.fypbackend.spring_boot.entity.Product;

@Service
public class ReportService {

    private static final DateTimeFormatter DAILY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter WEEKLY_FORMATTER = DateTimeFormatter.ofPattern("YYYY-'W'ww");
    private static final DateTimeFormatter MONTHLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final String DEFAULT_CURRENCY = "USD";
    private static final int DEFAULT_TREND_DAYS = 30;
    private static final int DEFAULT_ORDER_STATS_DAYS = 7;
    private static final int DEFAULT_TOP_PRODUCTS_LIMIT = 10;
    private static final int DEFAULT_INVENTORY_THRESHOLD = 10;
    private static final WeekFields WEEK_FIELDS = WeekFields.ISO;
    private static final DayOfWeek FIRST_DAY_OF_WEEK = WEEK_FIELDS.getFirstDayOfWeek();

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public ReportService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public SalesTrendResponse buildSalesTrend(String interval, LocalDate start, LocalDate end) {
        String normalizedInterval = normalizeInterval(interval);
        LocalDate resolvedEnd = end != null ? end : LocalDate.now();
        LocalDate resolvedStart = start != null ? start : resolvedEnd.minusDays(DEFAULT_TREND_DAYS - 1);

        if (resolvedStart.isAfter(resolvedEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }

        LocalDate periodStart = getPeriodStart(resolvedStart, normalizedInterval);
        LocalDate periodEnd = getPeriodStart(resolvedEnd, normalizedInterval);
        LocalDateTime queryStart = resolvedStart.atStartOfDay();
        LocalDateTime queryEnd = resolvedEnd.plusDays(1).atStartOfDay();

        Map<String, BigDecimal> totals = buildBigDecimalPeriodMap(periodStart, periodEnd, normalizedInterval);
        List<? extends SalesPeriodAggregation> aggregations = fetchSalesAggregation(normalizedInterval, queryStart, queryEnd);
        for (SalesPeriodAggregation aggregation : aggregations) {
            totals.computeIfPresent(aggregation.getPeriod(), (key, value) -> aggregation.getTotal());
        }

        return new SalesTrendResponse(
                normalizedInterval,
                resolvedStart,
                resolvedEnd,
                new ArrayList<>(totals.keySet()),
                new ArrayList<>(totals.values()),
                DEFAULT_CURRENCY);
    }

    public OrderStatisticsResponse buildOrderStatistics(String interval, LocalDate start, LocalDate end) {
        String normalizedInterval = normalizeOrderStatsInterval(interval);
        LocalDate resolvedEnd = end != null ? end : LocalDate.now();
        LocalDate resolvedStart = start != null ? start : resolvedEnd.minusDays(DEFAULT_ORDER_STATS_DAYS - 1);

        if (resolvedStart.isAfter(resolvedEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }

        LocalDate periodStart = getPeriodStart(resolvedStart, normalizedInterval);
        LocalDate periodEnd = getPeriodStart(resolvedEnd, normalizedInterval);
        LocalDateTime queryStart = resolvedStart.atStartOfDay();
        LocalDateTime queryEnd = resolvedEnd.plusDays(1).atStartOfDay();

        Map<String, Long> counts = buildLongPeriodMap(periodStart, periodEnd, normalizedInterval);
        List<OrderCountPeriodAggregation> aggregations = fetchOrderCountAggregation(normalizedInterval, queryStart, queryEnd);
        for (OrderCountPeriodAggregation aggregation : aggregations) {
            counts.computeIfPresent(aggregation.getPeriod(), (key, value) -> aggregation.getCount());
        }

        long totalOrders = counts.values().stream().mapToLong(Long::longValue).sum();

        return new OrderStatisticsResponse(
                normalizedInterval,
                resolvedStart,
                resolvedEnd,
                totalOrders,
                new ArrayList<>(counts.keySet()),
                new ArrayList<>(counts.values()));
    }

    public TopSellingProductResponse buildTopSellingProducts(LocalDate start, LocalDate end, Integer limit) {
        int resolvedLimit = limit != null ? limit : DEFAULT_TOP_PRODUCTS_LIMIT;
        if (resolvedLimit < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limit must be at least 1");
        }

        LocalDate resolvedEnd = end != null ? end : LocalDate.now();
        LocalDate resolvedStart = start != null ? start : resolvedEnd.minusDays(DEFAULT_TREND_DAYS - 1);
        if (resolvedStart.isAfter(resolvedEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }

        LocalDateTime queryStart = resolvedStart.atStartOfDay();
        LocalDateTime queryEnd = resolvedEnd.plusDays(1).atStartOfDay();
        Pageable pageable = PageRequest.of(0, resolvedLimit);
        List<TopSellingProductProjection> products = orderRepository.findTopSellingProducts(queryStart, queryEnd, pageable);

        List<TopSellingProductDto> productDtos = products.stream()
                .map(entry -> new TopSellingProductDto(
                        entry.getProductId(),
                        entry.getProductName(),
                        entry.getUnitsSold(),
                        entry.getRevenue()))
                .collect(Collectors.toList());

        return new TopSellingProductResponse(resolvedStart, resolvedEnd, resolvedLimit, productDtos);
    }

    public InventoryAlertResponse buildInventoryAlerts(Integer threshold) {
        int resolvedThreshold = threshold != null ? threshold : DEFAULT_INVENTORY_THRESHOLD;
        if (resolvedThreshold <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Threshold must be greater than zero");
        }

        List<Product> lowStock = productRepository.findByActiveTrueAndUnitsInStockLessThanEqualOrderByUnitsInStockAsc(resolvedThreshold);
        List<InventoryAlertDto> alerts = lowStock.stream()
                .map(product -> new InventoryAlertDto(
                        product.getId(),
                        product.getName(),
                        product.getUnitsInStock(),
                        product.getUnitPrice(),
                        product.isActive()))
                .collect(Collectors.toList());

        return new InventoryAlertResponse(resolvedThreshold, alerts);
    }

    public KpiResponse buildKpis(LocalDate start, LocalDate end) {
        LocalDate resolvedEnd = end != null ? end : LocalDate.now();
        LocalDate resolvedStart = start != null ? start : resolvedEnd.minusDays(DEFAULT_TREND_DAYS - 1);
        if (resolvedStart.isAfter(resolvedEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }

        LocalDateTime queryStart = resolvedStart.atStartOfDay();
        LocalDateTime queryEnd = resolvedEnd.plusDays(1).atStartOfDay();
        KpiProjection projection = orderRepository.summarizeKpis(queryStart, queryEnd);

        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal avgOrderValue = BigDecimal.ZERO;
        long totalOrders = 0;
        if (projection != null) {
            totalSales = projection.getTotalSales() != null ? projection.getTotalSales() : BigDecimal.ZERO;
            avgOrderValue = projection.getAvgOrderValue() != null ? projection.getAvgOrderValue() : BigDecimal.ZERO;
            totalOrders = projection.getTotalOrders() != null ? projection.getTotalOrders() : 0L;
        }

        return new KpiResponse(resolvedStart, resolvedEnd, totalSales, totalOrders, avgOrderValue);
    }

    private String normalizeInterval(String interval) {
        if (interval == null || interval.isBlank()) {
            return "daily";
        }

        String normalized = interval.toLowerCase(Locale.getDefault());
        return switch (normalized) {
            case "daily", "weekly", "monthly" -> normalized;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Interval must be 'daily', 'weekly' or 'monthly'");
        };
    }

    private String normalizeOrderStatsInterval(String interval) {
        if (interval == null || interval.isBlank()) {
            return "daily";
        }

        String normalized = interval.toLowerCase(Locale.getDefault());
        return switch (normalized) {
            case "daily", "weekly" -> normalized;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Order statistics interval must be 'daily' or 'weekly'");
        };
    }

    private Map<String, BigDecimal> buildBigDecimalPeriodMap(LocalDate start, LocalDate end, String interval) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        LocalDate cursor = start;
        LocalDate lastPeriod = end;
        while (!cursor.isAfter(lastPeriod)) {
            map.put(formatPeriodLabel(cursor, interval), BigDecimal.ZERO);
            cursor = incrementPeriod(cursor, interval);
        }
        return map;
    }

    private Map<String, Long> buildLongPeriodMap(LocalDate start, LocalDate end, String interval) {
        Map<String, Long> map = new LinkedHashMap<>();
        LocalDate cursor = start;
        LocalDate lastPeriod = end;
        while (!cursor.isAfter(lastPeriod)) {
            map.put(formatPeriodLabel(cursor, interval), 0L);
            cursor = incrementPeriod(cursor, interval);
        }
        return map;
    }

    private LocalDate getPeriodStart(LocalDate date, String interval) {
        return switch (interval) {
            case "weekly" -> date.with(WEEK_FIELDS.dayOfWeek(), FIRST_DAY_OF_WEEK.getValue());
            case "monthly" -> date.withDayOfMonth(1);
            default -> date;
        };
    }

    private LocalDate incrementPeriod(LocalDate date, String interval) {
        return switch (interval) {
            case "weekly" -> date.plusWeeks(1);
            case "monthly" -> date.plusMonths(1);
            default -> date.plusDays(1);
        };
    }

    private String formatPeriodLabel(LocalDate date, String interval) {
        return switch (interval) {
            case "weekly" -> formatWeekLabel(date);
            case "monthly" -> MONTHLY_FORMATTER.format(date);
            default -> DAILY_FORMATTER.format(date);
        };
    }

    private String formatWeekLabel(LocalDate date) {
        int weekYear = date.get(WEEK_FIELDS.weekBasedYear());
        int week = date.get(WEEK_FIELDS.weekOfWeekBasedYear());
        return String.format("%d-W%02d", weekYear, week);
    }

    private List<? extends SalesPeriodAggregation> fetchSalesAggregation(String interval, LocalDateTime start, LocalDateTime end) {
        return switch (interval) {
            case "weekly" -> orderRepository.sumWeeklySales(start, end);
            case "monthly" -> orderRepository.sumMonthlySales(start, end);
            default -> orderRepository.sumDailySales(start, end);
        };
    }

    private List<OrderCountPeriodAggregation> fetchOrderCountAggregation(String interval, LocalDateTime start,
            LocalDateTime end) {
        return switch (interval) {
            case "weekly" -> orderRepository.countWeeklyOrders(start, end);
            default -> orderRepository.countDailyOrders(start, end);
        };
    }

}
