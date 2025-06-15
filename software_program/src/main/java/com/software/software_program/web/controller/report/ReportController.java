package com.software.software_program.web.controller.report;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.core.utility.Formatter;
import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.DepartmentEntity;
import com.software.software_program.model.enums.ReportType;
import com.software.software_program.model.report.SoftwareCoverageInfo;
import com.software.software_program.model.report.SoftwareUsageInfo;
import com.software.software_program.repository.DepartmentRepository;
import com.software.software_program.service.entity.ClassroomService;
import com.software.software_program.service.entity.DepartmentService;
import com.software.software_program.service.report.*;
import com.software.software_program.web.dto.pagination.PageDto;
import com.software.software_program.web.dto.report.ReportDto;
import com.software.software_program.web.mapper.pagination.PageDtoMapper;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService service;
    private final ReportExporter exporter;
    private final PdfExportStrategy pdfStrategy;
    private final ClassroomService classroomService;
    private final DepartmentService departmentService;
    private final DepartmentRepository departmentRepository;

    @PostMapping("/download/pdf/{reportType}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable ReportType reportType, @RequestBody ReportDto reportDto) throws JRException {
        JasperPrint print = getJasperPrint(reportType, reportDto, false);
        exporter.setStrategy(pdfStrategy);
        byte[] file = exporter.export(print);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }

    @GetMapping("/coverage/grouped")
    public List<SoftwareCoverageInfo> getCoverageGrouped(@RequestParam(required = false) List<Long> classroomIds) {
        List<ClassroomEntity> classrooms = classroomIds != null && !classroomIds.isEmpty()
                ? classroomService.getByIds(classroomIds)
                : classroomService.getAll();
        return service.getCoverageInfo(classrooms);
    }

    @PostMapping("/html/{reportType}")
    public ResponseEntity<String> getHtml(@PathVariable ReportType reportType, @RequestBody ReportDto reportDto) throws JRException {
        JasperPrint print = getJasperPrint(reportType, reportDto, reportDto.isIgnorePagination());
        String html = exporter.getHtml(print);
        return ResponseEntity.ok(html);
    }

    @GetMapping(value = "/coverage/by-department", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SoftwareUsageInfo> getSoftwareCoverageByDepartment(
            @RequestParam String periodStart,
            @RequestParam String periodEnd,
            @RequestParam(required = false) List<Long> departmentIds
    ) {
        System.out.printf("Получен запрос: periodStart=%s, periodEnd=%s%n", periodStart, periodEnd);

        Date from = Formatter.parse(periodStart);
        Date to = Formatter.parse(periodEnd);

        List<DepartmentEntity> departments = departmentIds != null && !departmentIds.isEmpty()
                ? departmentService.getByIds(departmentIds)
                : departmentService.getAll();

        List<SoftwareUsageInfo> usageInfos = service.getUsageInfo(departments, from, to);

        System.out.println("➡️ Отдаётся количество записей: " + usageInfos.size());

        return usageInfos;
    }

//    @PostMapping("/coverage/by-department/paged")
//    public PageDto<SoftwareUsageInfo> getUsagePaged(
//            @RequestParam String periodStart,
//            @RequestParam String periodEnd,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size
//    ) {
//        Date from = Formatter.parse(periodStart);
//        Date to = Formatter.parse(periodEnd);
//        Pageable pageable = PageRequest.of(page, size);
//        Page<SoftwareUsageInfo> usagePage = service.getUsageInfoPaged(from, to, pageable);
//        return PageDtoMapper.toDto(usagePage, Function.identity());
//    }
//
//    @PostMapping("/coverage/grouped/paged")
//    public PageDto<SoftwareCoverageInfo> getCoveragePaged(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size
//    ) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<SoftwareCoverageInfo> coveragePage = service.getCoverageInfoPaged(pageable);
//        return PageDtoMapper.toDto(coveragePage, Function.identity());
//    }


    private JasperPrint getJasperPrint(ReportType reportType, ReportDto reportDto, boolean ignorePagination) throws JRException {
        return switch (reportType) {
            case SOFTWARE_REQUEST_FORM -> service.generateSoftwareRequestReport(reportDto, ignorePagination);
            case SOFTWARE_COVERAGE_REPORT -> service.generateSoftwareCoverageReport(reportDto, ignorePagination);
            case SOFTWARE_USAGE_REPORT -> service.generateSoftwareUsageReport(reportDto, ignorePagination);
        };
    }
}
