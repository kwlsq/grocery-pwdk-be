package com.pwdk.grocereach.order.presentations.dtos.sales;

public record MonthlyOrder(
    String month,
    Long orderCount
) {}
