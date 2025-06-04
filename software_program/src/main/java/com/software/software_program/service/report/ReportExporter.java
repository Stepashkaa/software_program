package com.software.software_program.service.report;

import lombok.Getter;
import lombok.Setter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;

import net.sf.jasperreports.web.util.WebHtmlResourceHandler;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

@Component
@Getter
@Setter
public class ReportExporter {
    private ReportExporterStrategy strategy;

    public byte[] export(JasperPrint jasperPrint) throws JRException {
        return strategy.export(jasperPrint);
    }

    public String getHtml(JasperPrint jasperPrint) throws JRException {
        HtmlExporter exporter = new HtmlExporter();
        StringWriter writer = new StringWriter();
        SimpleHtmlExporterOutput output = new SimpleHtmlExporterOutput(writer);

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(output);
        exporter.exportReport();

        return writer.toString();
    }
}
