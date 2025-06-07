package com.software.software_program.core.setup;

import com.software.software_program.core.configuration.AppConfigurationProperties;
import com.software.software_program.model.entity.*;
import com.software.software_program.model.enums.RequestStatus;
import com.software.software_program.model.enums.UserRole;
import com.software.software_program.service.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.software.software_program.core.log.Loggable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class EntityInitializer {

    private final FacultyService facultyService;
    private final DepartmentService departmentService;
    private final ClassroomService classroomService;
    private final SoftwareService softwareService;
    private final EquipmentService equipmentService;
    private final ClassroomSoftwareService classroomSoftwareService;
    private final SoftwareRequestService softwareRequestService;
    private final UserService userService;

    @Loggable
    @Transactional
    public void initializeAll() {
        List<FacultyEntity> faculties = createFaculties();
        List<DepartmentEntity> departments = createDepartments(faculties);
        List<ClassroomEntity> classrooms = createClassrooms(departments);
        List<SoftwareEntity> softwares = createSoftwares();
        List<EquipmentEntity> equipments = createEquipments(classrooms);
        associateClassroomSoftware(classrooms, softwares);
        createUser();
        createSoftwareRequests(classrooms, softwares);
    }

    @Loggable
    private List<FacultyEntity> createFaculties() {
        List<FacultyEntity> faculties = new ArrayList<>();
        faculties.add(facultyService.create(new FacultyEntity("Факультет информационных технологий")));
        faculties.add(facultyService.create(new FacultyEntity("Факультет экономики и управления")));
        return faculties;
    }

    @Loggable
    private List<DepartmentEntity> createDepartments(List<FacultyEntity> faculties) {
        List<DepartmentEntity> departments = new ArrayList<>();
        departments.add(departmentService.create(new DepartmentEntity("Кафедра программирования", faculties.get(0))));
        departments.add(departmentService.create(new DepartmentEntity("Кафедра системного администрирования", faculties.get(0))));
        departments.add(departmentService.create(new DepartmentEntity("Кафедра экономики", faculties.get(1))));
        return departments;
    }

    @Loggable
    private List<ClassroomEntity> createClassrooms(List<DepartmentEntity> departments) {
        List<ClassroomEntity> classrooms = new ArrayList<>();
        classrooms.add(classroomService.create(new ClassroomEntity("Аудитория 101", 30, departments.get(0))));
        classrooms.add(classroomService.create(new ClassroomEntity("Аудитория 202", 25, departments.get(1))));
        classrooms.add(classroomService.create(new ClassroomEntity("Аудитория 303", 40, departments.get(2))));
        return classrooms;
    }

    @Loggable
    private List<SoftwareEntity> createSoftwares() {
        List<SoftwareEntity> softwares = new ArrayList<>();
        softwares.add(softwareService.create(new SoftwareEntity("Microsoft Office", "2021", "Пакет офисных приложений")));
        softwares.add(softwareService.create(new SoftwareEntity("IntelliJ IDEA", "2023.1", "IDE для разработки на Java")));
        softwares.add(softwareService.create(new SoftwareEntity("AutoCAD", "2023", "Система автоматизированного проектирования")));
        return softwares;
    }

    @Loggable
    private List<EquipmentEntity> createEquipments(List<ClassroomEntity> classrooms) {
        List<EquipmentEntity> equipments = new ArrayList<>();
        equipments.add(equipmentService.create(new EquipmentEntity("Проектор", "Мультимедийный", "PRJ12345", classrooms.get(0))));
        equipments.add(equipmentService.create(new EquipmentEntity("Компьютер", "Рабочая станция", "PC67890", classrooms.get(1))));
        equipments.add(equipmentService.create(new EquipmentEntity("Интерактивная доска", "Образовательная", "BD09876", classrooms.get(2))));
        return equipments;
    }

    @Loggable
    private void associateClassroomSoftware(List<ClassroomEntity> classrooms, List<SoftwareEntity> softwares) {
        ClassroomSoftwareEntity classroomSoftware1 = new ClassroomSoftwareEntity(
                classrooms.get(0), softwares.get(0), new Date());
        classroomSoftwareService.create(classroomSoftware1);

        ClassroomSoftwareEntity classroomSoftware2 = new ClassroomSoftwareEntity(
                classrooms.get(1), softwares.get(1), new Date());
        classroomSoftwareService.create(classroomSoftware2);

        ClassroomSoftwareEntity classroomSoftware3 = new ClassroomSoftwareEntity(
                classrooms.get(2), softwares.get(2), new Date());
        classroomSoftwareService.create(classroomSoftware3);
    }

    @Loggable
    private void createUser() {
        UserEntity user = new UserEntity();
        user.setEmail("admin@example.com");
        user.setPassword("password");
        user.setRole(UserRole.ADMIN);
        userService.create(user);
    }

    @Loggable
    private void createSoftwareRequests(List<ClassroomEntity> classrooms, List<SoftwareEntity> softwares) {
        UserEntity user = userService.getByEmail("admin@example.com");

        // Создаем ClassroomSoftware для использования в заявках
        ClassroomSoftwareEntity classroomSoftware1 = classroomSoftwareService.getAll().get(0);
        ClassroomSoftwareEntity classroomSoftware2 = classroomSoftwareService.getAll().get(1);

        // Создаем заявки на установку ПО
        softwareRequestService.create(
                new Date(),
                "Заявка на установку Microsoft Office",
                user,
                classroomSoftware1
        );

        softwareRequestService.create(
                new Date(),
                "Заявка на установку IntelliJ IDEA",
                user,
                classroomSoftware2
        );
    }
}