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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        Map<String, Object> params = new HashMap<>();
        if (ignorePagination) {
            params.put(JRParameter.IS_IGNORE_PAGINATION, true);
        }

        return JasperFillManager.fillReport(
                jasperReport,
                params,
                new JRBeanCollectionDataSource(requestsInfo)
        );
    }

    public JasperPrint generateSoftwareCoverageReport(ReportDto reportDto, boolean ignorePagination) throws JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(
                getReportTemplateStream("SoftwareCoverage.jrxml"));

        List<SoftwareCoverageInfo> coverageInfo = getCoverageInfo(
                reportDto.getClassroomIds() != null && !reportDto.getClassroomIds().isEmpty()
                        ? classroomService.getByIds(reportDto.getClassroomIds())
                        : classroomService.getAll()
        );

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

        List<DepartmentEntity> departments =
                (reportDto.getDepartmentIds() != null && !reportDto.getDepartmentIds().isEmpty())
                        ? departmentService.getByIds(reportDto.getDepartmentIds())
                        : departmentService.getAll();
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
        List<SoftwareEntity> allSoftware = softwareService.getAll();

        return requests.stream()
                .flatMap(request -> request.getRequestItems().stream().map(item -> {
                    SoftwareRequestInfo info = new SoftwareRequestInfo();

                    // Оборудование
                    info.setSerialNumber(item.getSerialNumber());
                    info.setEquipmentName(item.getEquipmentName());

                    // ПО
                    info.setSoftwareName(item.getSoftwareName());
                    info.setSoftwareDescription(item.getSoftwareDescription());
                    info.setSoftwareType(item.getSoftwareType());

                    // Наличие ПО (по совпадению имени и описания в базе)
                    boolean exists = allSoftware.stream().anyMatch(software ->
                            software.getName().equalsIgnoreCase(item.getSoftwareName()) &&
                                    software.getDescription().equalsIgnoreCase(item.getSoftwareDescription())
                    );
                    info.setAvailability(exists ? "+" : "–");

                    return info;
                }))
                .collect(Collectors.toList());
    }



    public List<SoftwareCoverageInfo> getCoverageInfo(List<ClassroomEntity> classrooms) {
        return classrooms.stream().map(classroom -> {
            SoftwareCoverageInfo info = new SoftwareCoverageInfo();
            info.setClassroomId(classroom.getId());
            info.setClassroomName(classroom.getName());
            info.setClassroomCapacity(classroom.getCapacity());
            info.setDepartmentName(classroom.getDepartment().getName());
            info.setFacultyName(classroom.getDepartment().getFaculty().getName());

            List<SoftwareDto> softwareList = classroom.getEquipments().stream()
                    .flatMap(equipment -> equipment.getEquipmentSoftwares().stream())
                    .flatMap(es -> es.getSoftwares() != null
                            ? es.getSoftwares().stream()
                            : Stream.empty())
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(
                                    s -> s.getName() + "_" + s.getVersion(),
                                    s -> new SoftwareDto(s.getName(), s.getVersion(), s.getDescription(), s.getType()),
                                    (s1, s2) -> s1
                            ),
                            map -> new ArrayList<>(map.values())
                    ));

            info.setSoftwareList(softwareList);
            info.setFullyCovered(!softwareList.isEmpty());
            return info;
        }).collect(Collectors.toList());
    }

    public List<SoftwareUsageInfo> getUsageInfo(List<DepartmentEntity> departments, Date from, Date to) {
        if (departments == null) {
            departments = departmentService.getAll();
        }

        LocalDate fromDate = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toDate = to.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return departments.stream().map(department -> {
            SoftwareUsageInfo info = new SoftwareUsageInfo();
            info.setDepartmentId(department.getId());
            info.setDepartmentName(department.getName());
            info.setFacultyName(department.getFaculty().getName());

            Set<String> classroomNames = new HashSet<>();
            Map<String, SoftwareUsageItem> softwareMap = new LinkedHashMap<>();

            department.getClassrooms().forEach(classroom -> {
                classroomNames.add(classroom.getName());

                classroom.getEquipments().forEach(equipment -> {
                    equipment.getEquipmentSoftwares().stream()
                            .filter(es -> {
                                if (es.getInstallationDate() == null) return false;

                                LocalDate installDate = es.getInstallationDate().toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();

                                boolean inRange = !installDate.isBefore(fromDate) && !installDate.isAfter(toDate);
                                System.out.printf("ПО [%s] дата установки: %s → входит в [%s — %s]? %s%n",
                                        es.getSoftwares().stream().map(SoftwareEntity::getName).toList(),
                                        installDate, fromDate, toDate, inRange);
                                return inRange;
                            })
                            .forEach(es -> {
                                Date installationDate = es.getInstallationDate();
                                if (es.getSoftwares() != null) {
                                    es.getSoftwares().forEach(software -> {
                                        String key = software.getName() + "_" + software.getVersion();
                                        if (!softwareMap.containsKey(key)) {
                                            SoftwareUsageItem item = new SoftwareUsageItem();
                                            item.setSoftwareName(software.getName());
                                            item.setVersion(software.getVersion());
                                            item.setClassroomName(classroom.getName());
                                            item.setInstallationDate(installationDate);
                                            softwareMap.put(key, item);
                                        }
                                    });
                                }
                            });
                });
            });

            info.setClassroomNames(new ArrayList<>(classroomNames));
            info.setSoftwareList(new ArrayList<>(softwareMap.values()));
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