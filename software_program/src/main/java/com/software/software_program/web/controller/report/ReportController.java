package com.software.software_program.web.controller.report;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.model.enums.ReportType;
import com.software.software_program.service.report.*;
import com.software.software_program.web.dto.report.ReportDto;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService service;
    private final ReportExporter exporter;
    private final PdfExportStrategy pdfStrategy;
//    private final XlsxExportStrategy xlsxStrategy;
//    private final DocxExportStrategy docxStrategy;

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

//    @PostMapping("/download/docx/{reportType}")
//    public ResponseEntity<byte[]> downloadDocx(@PathVariable ReportType reportType, @RequestBody ReportDto reportDto) throws Exception {
//        JasperPrint print = getJasperPrint(reportType, reportDto, false);
//        exporter.setStrategy(docxStrategy);
//        byte[] file = exporter.export(print);
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.docx")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(file);
//    }

//    @PostMapping("/download/xlsx/{reportType}")
//    public ResponseEntity<byte[]> downloadXlsx(@PathVariable ReportType reportType, @RequestBody ReportDto reportDto) throws JRException {
//        JasperPrint print = getJasperPrint(reportType, reportDto, true);
//        exporter.setStrategy(xlsxStrategy);
//        byte[] file = exporter.export(print);
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.xlsx")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(file);
//    }

    @PostMapping("/html/{reportType}")
    public ResponseEntity<String> getHtml(@PathVariable ReportType reportType, @RequestBody ReportDto reportDto) throws JRException {
        JasperPrint print = getJasperPrint(reportType, reportDto, true);
        String html = exporter.getHtml(print);
        return ResponseEntity.ok(html);
    }

    private JasperPrint getJasperPrint(ReportType reportType, ReportDto reportDto, boolean ignorePagination) throws JRException {
        return switch (reportType) {
            case SOFTWARE_REQUEST_FORM -> service.generateSoftwareRequestReport(reportDto, ignorePagination);
            case SOFTWARE_COVERAGE_REPORT -> service.generateSoftwareCoverageReport(reportDto, ignorePagination);
            case SOFTWARE_USAGE_REPORT -> service.generateSoftwareUsageReport(reportDto, ignorePagination);
        };
    }
}
