package com.pwdk.grocereach.inventory.presentations.dtos;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class YearMonthConverter implements Converter<String, YearMonth> {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    
    @Override
    public YearMonth convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            return YearMonth.parse(source.trim(), FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid year-month format. Expected format: yyyy-MM", e);
        }
    }
}
