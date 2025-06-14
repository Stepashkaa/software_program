package com.software.software_program.service.report;

import com.software.software_program.core.utility.Formatter;
import com.software.software_program.model.entity.*;
import com.software.software_program.model.report.*;
import com.software.software_program.service.entity.*;
import com.software.software_program.web.dto.entity.SoftwareDto;
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

            EquipmentEntity equipment = request.getEquipment();
            SoftwareEntity software = request.getSoftware();
            ClassroomEntity classroom = equipment.getClassroom();

            info.setRequestNumber(request.getId().toString());
            info.setRequestDate(Formatter.formatToCustomString(request.getRequestDate()));
            info.setStatus(request.getStatus().toString());
            info.setDescription(request.getDescription());

            if (software != null) {
                info.setSoftwareName(software.getName());
                info.setSoftwareVersion(software.getVersion());
                info.setSoftwareDescription(software.getDescription());
            } else {
                info.setSoftwareName(request.getRequestedSoftwareName() != null ? request.getRequestedSoftwareName() : "—");
                info.setSoftwareVersion("—");
                info.setSoftwareDescription("Не выбрано из базы");
            }

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

            List<SoftwareDto> softwareList = classroom.getEquipments().stream()
                    .flatMap(equipment -> equipment.getEquipmentSoftwares().stream())
                    .flatMap(es -> es.getSoftwares().stream())
                    .map(software -> new SoftwareDto(
                            software.getName(),
                            software.getVersion(),
                            software.getDescription(),
                            software.getType()))
                    .distinct()
                    .collect(Collectors.toList());

            info.setSoftwareList(softwareList);
            info.setFullyCovered(!softwareList.isEmpty());
            return info;
        }).collect(Collectors.toList());
    }



    private List<SoftwareUsageInfo> getUsageInfo(List<DepartmentEntity> departments,
                                                 Date from, Date to) {
        return departments.stream().map(department -> {
            SoftwareUsageInfo info = new SoftwareUsageInfo();
            info.setDepartmentName(department.getName());
            info.setFacultyName(department.getFaculty().getName());

            List<SoftwareUsageItem> usageItems = department.getClassrooms().stream()
                    .flatMap(classroom -> classroom.getEquipments().stream()
                            .flatMap(equipment -> equipment.getEquipmentSoftwares().stream()
                                    .filter(es -> !es.getInstallationDate().before(from)
                                            && !es.getInstallationDate().after(to))
                                    .flatMap(es -> es.getSoftwares().stream()
                                            .map(software -> {
                                                SoftwareUsageItem item = new SoftwareUsageItem();
                                                item.setSoftwareName(software.getName());
                                                item.setVersion(software.getVersion());
                                                item.setClassroomName(classroom.getName());
                                                item.setInstallationDate(es.getInstallationDate());
                                                return item;
                                            }))
                            )
                    )
                    .collect(Collectors.toList());

            info.setSoftwareList(usageItems);
            info.setReportPeriodStart(from);
            info.setReportPeriodEnd(to);
            return info;
        }).collect(Collectors.toList());
    }



    private boolean checkCoverage(ClassroomEntity classroom) {
        return classroom.getEquipments().stream()
                .anyMatch(equipment -> !equipment.getEquipmentSoftwares().isEmpty());
    }


    private InputStream getReportTemplateStream(String fileName) {
        return getClass().getResourceAsStream("/static/report/" + fileName);
    }

}