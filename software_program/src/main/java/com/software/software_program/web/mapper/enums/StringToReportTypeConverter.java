package com.software.software_program.web.mapper.enums;

import com.software.software_program.model.enums.ReportType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToReportTypeConverter implements Converter<String, ReportType> {
    @Override
    public ReportType convert(String source) {
        return switch (source.toLowerCase()) {
            case "software-request-form" -> ReportType.SOFTWARE_REQUEST_FORM;
            case "software-coverage-report" -> ReportType.SOFTWARE_COVERAGE_REPORT;
            case "software-usage-report" -> ReportType.SOFTWARE_USAGE_REPORT;
            default -> throw new IllegalArgumentException("Unknown report type: " + source);
        };
    }
}
