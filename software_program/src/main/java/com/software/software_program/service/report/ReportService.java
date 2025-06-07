package com.software.software_program.service.report;

import com.software.software_program.core.utility.Formatter;
import com.software.software_program.model.entity.*;
import com.software.software_program.model.report.*;
import com.software.software_program.service.entity.*;
import com.software.software_program.web.dto.report.ReportDto;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final SoftwareService softwareService;
    private final ClassroomService classroomService;
    private final DepartmentService departmentService;
    private final SoftwareRequestService requestService;

    public JasperPrint generateSoftwareRequestReport(ReportDto reportDto, boolean ignorePagination) throws JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(
                getReportTemplateStream("SoftwareRequest.jrxml"));

        List<SoftwareRequestInfo> requestsInfo = getRequestsInfo(
                requestService.getByIds(reportDto.getRequestIds()));

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(requestsInfo);
        Map<String, Object> params = new HashMap<>();
        if (ignorePagination) {
            params.put(JRParameter.IS_IGNORE_PAGINATION, true);
        }
        return JasperFillManager.fillReport(jasperReport, params, dataSource);
    }

    public JasperPrint generateSoftwareCoverageReport(ReportDto reportDto, boolean ignorePagination) throws JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(
                getReportTemplateStream("SoftwareCoverage.jrxml"));

        List<ClassroomEntity> classrooms = classroomService.getByIds(reportDto.getClassroomIds());
        List<SoftwareCoverageInfo> coverageInfo = getCoverageInfo(classrooms);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(coverageInfo);
        Map<String, Object> params = new HashMap<>();
        params.put("reportDate", Formatter.formatToCustomString(LocalDate.now()));
        if (ignorePagination) {
            params.put(JRParameter.IS_IGNORE_PAGINATION, true);
        }
        return JasperFillManager.fillReport(jasperReport, params, dataSource);
    }

    public JasperPrint generateSoftwareUsageReport(ReportDto reportDto, boolean ignorePagination) throws JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(
                getReportTemplateStream("SoftwareUsage.jrxml"));

        Date dateFrom = Formatter.parse(reportDto.getPeriodStart());
        Date dateTo = Formatter.parse(reportDto.getPeriodEnd());

        List<DepartmentEntity> departments = departmentService.getByIds(reportDto.getDepartmentIds());
        List<SoftwareUsageInfo> usageInfo = getUsageInfo(departments, dateFrom, dateTo);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(usageInfo);
        Map<String, Object> params = new HashMap<>();
        params.put("periodFrom", Formatter.formatToCustomString(dateFrom));
        params.put("periodTo", Formatter.formatToCustomString(dateTo));
        if (ignorePagination) {
            params.put(JRParameter.IS_IGNORE_PAGINATION, true);
        }
        return JasperFillManager.fillReport(jasperReport, params, dataSource);
    }

    private List<SoftwareRequestInfo> getRequestsInfo(List<SoftwareRequestEntity> requests) {
        return requests.stream().map(request -> {
            SoftwareRequestInfo info = new SoftwareRequestInfo();
            ClassroomSoftwareEntity cs = request.getClassroomSoftware();
            ClassroomEntity classroom = cs.getClassroom();

            info.setRequestNumber(request.getId().toString());
            info.setRequestDate(Formatter.formatToCustomString(request.getRequestDate()));
            info.setStatus(request.getStatus().toString());
            info.setDescription(request.getDescription());

            info.setSoftwareName(cs.getSoftware().getName());
            info.setSoftwareVersion(cs.getSoftware().getVersion());
            info.setSoftwareDescription(cs.getSoftware().getDescription());

            info.setClassroomName(classroom.getName());
            info.setDepartmentName(classroom.getDepartment().getName());
            info.setFacultyName(classroom.getDepartment().getFaculty().getName());

            info.setRequesterName(request.getUser().getFullName());

            return info;
        }).collect(Collectors.toList());
    }

    private List<SoftwareCoverageInfo> getCoverageInfo(List<ClassroomEntity> classrooms) {
        return classrooms.stream().map(classroom -> {
            SoftwareCoverageInfo info = new SoftwareCoverageInfo();
            info.setClassroomName(classroom.getName());
            info.setClassroomCapacity(classroom.getCapacity());
            info.setDepartmentName(classroom.getDepartment().getName());
            info.setFacultyName(classroom.getDepartment().getFaculty().getName());

            List<SoftwareItem> softwareItems =
                    classroom.getClassroomSoftwares().stream()
                            .map(cs -> {
                                SoftwareItem item = new SoftwareItem();  // Создаем экземпляр отдельного класса
                                item.setName(cs.getSoftware().getName());
                                item.setVersion(cs.getSoftware().getVersion());
                                item.setInstallationDate(cs.getInstallationDate());
                                return item;
                            })
                            .collect(Collectors.toList());

            info.setSoftwareList(softwareItems);
            info.setFullyCovered(checkCoverage(classroom));
            return info;
        }).collect(Collectors.toList());
    }

    private List<SoftwareUsageInfo> getUsageInfo(List<DepartmentEntity> departments,
                                                 Date from, Date to) {
        return departments.stream().map(department -> {
            SoftwareUsageInfo info = new SoftwareUsageInfo();
            info.setDepartmentName(department.getName());
            info.setFacultyName(department.getFaculty().getName());

            List<SoftwareUsageItem> usageItems = department.getClassrooms()
                    .stream()
                    .flatMap(classroom -> classroom.getClassroomSoftwares().stream()
                            .filter(cs -> !cs.getInstallationDate().before(from) &&
                                    !cs.getInstallationDate().after(to))
                            .map(cs -> {
                                SoftwareUsageItem item = new SoftwareUsageItem();
                                item.setSoftwareName(cs.getSoftware().getName());
                                item.setVersion(cs.getSoftware().getVersion());
                                item.setClassroomName(cs.getClassroom().getName());
                                item.setInstallationDate(cs.getInstallationDate());
                                return item;
                            }))
                    .collect(Collectors.toList());

            info.setSoftwareList(usageItems);
            info.setReportPeriodStart(from);
            info.setReportPeriodEnd(to);
            return info;
        }).collect(Collectors.toList());
    }

    private boolean checkCoverage(ClassroomEntity classroom) {
        return !classroom.getClassroomSoftwares().isEmpty();
    }

    private InputStream getReportTemplateStream(String fileName) {
        return getClass().getResourceAsStream("/static/report/" + fileName);
    }
}