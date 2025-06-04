package com.software.software_program.service.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

public interface ReportExporterStrategy {
    byte[] export(JasperPrint jasperPrint) throws JRException;
}

