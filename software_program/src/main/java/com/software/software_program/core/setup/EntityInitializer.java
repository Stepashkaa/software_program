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
    private final AppConfigurationProperties appConfigurationProperties;

    @Loggable
    @Transactional
    public void initializeAll() {
        createUser();
        List<FacultyEntity> faculties = createFaculties();
        List<UserEntity> heads = createHeads(); // Создаем список заведующих
        List<DepartmentEntity> departments = createDepartments(faculties, heads); // Передаем заведующих
        List<ClassroomEntity> classrooms = createClassrooms(departments);
        List<SoftwareEntity> softwares = createSoftwares();
        List<EquipmentEntity> equipments = createEquipments(classrooms);
        associateClassroomSoftware(classrooms, softwares);
        // createSoftwareRequests(classrooms, softwares);
    }

    @Loggable
    private List<FacultyEntity> createFaculties() {
        List<FacultyEntity> faculties = new ArrayList<>();
        faculties.add(facultyService.create(new FacultyEntity("Факультет информационных технологий")));
        faculties.add(facultyService.create(new FacultyEntity("Факультет экономики и управления")));
        return faculties;
    }

    @Loggable
    private List<UserEntity> createHeads() {
        List<UserEntity> heads = new ArrayList<>();
        heads.add(createUser("John Doe", "johndoe@gmail.com", "+72345678956", "A1b@C2d#", UserRole.TEACHER));
        heads.add(createUser("Jane Smith", "janesmith@gmail.com", "+78765432189", "Qw3rty!*", UserRole.TEACHER));
        heads.add(createUser("Alice Johnson", "stepan2004stepan@yandex.ru", "+75553332156", "Zx%19cvB", UserRole.TEACHER));
        return heads;
    }

    // Вспомогательный метод для создания пользователя
    private UserEntity createUser(String fullName, String email, String phoneNumber, String password, UserRole role) {
        UserEntity user = new UserEntity(fullName, email, phoneNumber, password, role);
        return userService.create(user);
    }

    // Вспомогательный метод для создания кафедры с заведующим
    private DepartmentEntity createDepartment(String name, FacultyEntity faculty, UserEntity head) {
        DepartmentEntity department = new DepartmentEntity();
        department.setName(name);
        department.setFaculty(faculty);
        department.setHead(head); // Назначаем заведующего
        return departmentService.create(department);
    }

    @Loggable
    private List<DepartmentEntity> createDepartments(List<FacultyEntity> faculties, List<UserEntity> heads) {
        List<DepartmentEntity> departments = new ArrayList<>();
        departments.add(createDepartment("Кафедра программирования", faculties.get(0), heads.get(0)));
        departments.add(createDepartment("Кафедра системного администрирования", faculties.get(0), heads.get(1)));
        departments.add(createDepartment("Кафедра экономики", faculties.get(1), heads.get(2)));
        return departments;
    }

    @Loggable
    private List<ClassroomEntity> createClassrooms(List<DepartmentEntity> departments) {
        List<ClassroomEntity> classrooms = new ArrayList<>();
        classrooms.add(classroomService.create(new ClassroomEntity("Аудитория 101", 30, departments.get(0))));
        classrooms.add(classroomService.create(new ClassroomEntity("Аудитория 202", 25, departments.get(0))));
        classrooms.add(classroomService.create(new ClassroomEntity("Аудитория 303", 40, departments.get(1))));
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
        String email = appConfigurationProperties.getAdmin().getEmail();
        System.out.println("CREATING USER WITH EMAIL: " + email);

        userService.create(new UserEntity(
                "Admin",
                email,
                appConfigurationProperties.getAdmin().getNumber(),
                appConfigurationProperties.getAdmin().getPassword(),
                UserRole.SUPER_ADMIN
        ));
    }
}