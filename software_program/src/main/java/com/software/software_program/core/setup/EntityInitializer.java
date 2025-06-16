package com.software.software_program.core.setup;

import com.software.software_program.core.configuration.AppConfigurationProperties;
import com.software.software_program.model.entity.*;
import com.software.software_program.model.enums.RequestStatus;
import com.software.software_program.model.enums.TypeStatus;
import com.software.software_program.model.enums.UserRole;
import com.software.software_program.service.entity.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private static final Logger logger = LoggerFactory.getLogger(EntityInitializer.class);

    private final PasswordEncoder passwordEncoder;
    private final FacultyService facultyService;
    private final DepartmentService departmentService;
    private final ClassroomService classroomService;
    private final SoftwareService softwareService;
    private final EquipmentService equipmentService;
    private final EquipmentSoftwareService equipmentSoftwareService;
    private final SoftwareRequestService softwareRequestService;
    private final UserService userService;
    private final AppConfigurationProperties appConfigurationProperties;

    @Loggable
    @Transactional
    public void initializeAll() {
        logger.info("Initializing database...");

        List<FacultyEntity> faculties = createFaculties();
        if (faculties.isEmpty()) logger.warn("No faculties created or found.");

        List<UserEntity> heads = createHeads();
        if (heads.isEmpty()) logger.warn("No teachers created.");

        List<DepartmentEntity> departments = createDepartments(faculties, heads);
        if (departments.isEmpty()) logger.warn("No departments created or found.");

        List<ClassroomEntity> classrooms = createClassrooms(departments);
        if (classrooms.isEmpty()) logger.warn("No classrooms created.");

        List<SoftwareEntity> softwares = createSoftwares();
        if (softwares.isEmpty()) logger.warn("No softwares created.");

        List<EquipmentEntity> equipments = createEquipments(classrooms);
        if (equipments.isEmpty()) logger.warn("No equipments created.");

        associateEquipmentSoftware(equipments, softwares);




        List<UserEntity> users = createUsers();
        if (users.isEmpty()) logger.warn("No users created.");

        createSoftwareRequests(users, equipments, softwares);

        logger.info("Database initialization completed.");
    }

    @Loggable
    private List<FacultyEntity> createFaculties() {
        List<FacultyEntity> faculties = new ArrayList<>();
        if (facultyService.getAll("").isEmpty()) {
            faculties.add(facultyService.create(new FacultyEntity("Факультет информационных технологий")));
            faculties.add(facultyService.create(new FacultyEntity("Факультет экономики и управления")));
            logger.info("Faculties created.");
        }
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

    private UserEntity createUser(String fullName, String email, String phoneNumber, String password, UserRole role) {
        UserEntity user = new UserEntity(fullName, email, phoneNumber, passwordEncoder.encode(password), role);
        return userService.create(user);
    }


    private DepartmentEntity createDepartment(String name, FacultyEntity faculty, UserEntity head) {
        DepartmentEntity department = new DepartmentEntity();
        department.setName(name);
        department.setFaculty(faculty);
        department.setHead(head);
        return departmentService.create(department);
    }

    @Loggable
    private List<DepartmentEntity> createDepartments(List<FacultyEntity> faculties, List<UserEntity> heads) {
        List<DepartmentEntity> departments = new ArrayList<>();
        if (departmentService.getAll("").isEmpty()) {
            departments.add(createDepartment("Кафедра программирования", faculties.get(0), heads.get(0)));
            departments.add(createDepartment("Кафедра системного администрирования", faculties.get(0), heads.get(1)));
            departments.add(createDepartment("Кафедра экономики", faculties.get(1), heads.get(2)));
            logger.info("Departments created.");
        }
        return departments;
    }

    @Loggable
    private List<ClassroomEntity> createClassrooms(List<DepartmentEntity> departments) {
        List<ClassroomEntity> classrooms = new ArrayList<>();
        if (classroomService.getAll("").isEmpty()) {
            classrooms.add(classroomService.create(new ClassroomEntity("Аудитория 101", 30, departments.get(0))));
            classrooms.add(classroomService.create(new ClassroomEntity("Аудитория 202", 25, departments.get(0))));
            classrooms.add(classroomService.create(new ClassroomEntity("Аудитория 303", 40, departments.get(1))));
            logger.info("Classrooms created.");
        }
        return classrooms;
    }

    @Loggable
    private List<SoftwareEntity> createSoftwares() {
        List<SoftwareEntity> softwares = new ArrayList<>();
        if (softwareService.getAll("").isEmpty()) {
            softwares.add(softwareService.create(new SoftwareEntity("Microsoft Office", "2021", "Пакет офисных приложений", TypeStatus.FREE)));
            softwares.add(softwareService.create(new SoftwareEntity("IntelliJ IDEA", "2023.1", "IDE для разработки на Java", TypeStatus.GPL)));
            softwares.add(softwareService.create(new SoftwareEntity("AutoCAD", "2023", "САПР-система", TypeStatus.BUY)));
            logger.info("Softwares created.");
        }
        return softwares;
    }

    @Loggable
    private List<EquipmentEntity> createEquipments(List<ClassroomEntity> classrooms) {
        List<EquipmentEntity> equipments = new ArrayList<>();
        if (equipmentService.getAll("").isEmpty()) {
            equipments.add(equipmentService.create(new EquipmentEntity("Проектор", "Мультимедийный", "PRJ12345", classrooms.get(0))));
            equipments.add(equipmentService.create(new EquipmentEntity("Компьютер", "Рабочая станция", "PC67890", classrooms.get(1))));
            equipments.add(equipmentService.create(new EquipmentEntity("Интерактивная доска", "Образовательная", "BD09876", classrooms.get(2))));
            logger.info("Equipments created.");
        }
        return equipments;
    }

    @Loggable
    private void associateEquipmentSoftware(List<EquipmentEntity> equipments, List<SoftwareEntity> softwares) {
        // Например: на проектор — Office и AutoCAD
        equipmentSoftwareService.create(new EquipmentSoftwareEntity(
                equipments.get(0),
                Set.of(softwares.get(0), softwares.get(2)), // Microsoft Office, AutoCAD
                new Date()
        ));

        // На компьютер — IntelliJ IDEA и AutoCAD
        equipmentSoftwareService.create(new EquipmentSoftwareEntity(
                equipments.get(1),
                Set.of(softwares.get(1), softwares.get(2)), // IntelliJ IDEA, AutoCAD
                new Date()
        ));

        // На интерактивную доску — только Microsoft Office
        equipmentSoftwareService.create(new EquipmentSoftwareEntity(
                equipments.get(2),
                Set.of(softwares.get(0)), // Microsoft Office
                new Date()
        ));

        logger.info("Программное обеспечение успешно привязано к оборудованию.");
    }

    @Loggable
    private void createSoftwareRequests(List<UserEntity> users, List<EquipmentEntity> equipments, List<SoftwareEntity> softwares) {
        if (softwareRequestService.getAll(null, null).isEmpty()) {
            SoftwareRequestEntity request = new SoftwareRequestEntity();
            request.setRequestDate(new Date());
            request.setDescription("Автоматически созданная тестовая заявка");
            request.setUser(users.get(0)); // Первый созданный пользователь (например, админ)
            request.setStatus(RequestStatus.PENDING); // можно не указывать — выставляется по умолчанию

            List<SoftwareRequestItemEntity> items = new ArrayList<>();

            items.add(createRequestItem("Компьютер", "PC67890", "IntelliJ IDEA", "IDE для Java", "GPL", "Да"));
            items.add(createRequestItem("Компьютер", "PC67890", "IntelliJ2 IDEA2", "IDE для Java2", "GPL", "Нет"));
            items.add(createRequestItem("Компьютер2", "PC67899", "IntelliJ3 IDEA3", "IDE для Java3", "GPL", "Нет"));

            for (SoftwareRequestItemEntity item : items) {
                item.setRequest(request); // установить обратную связь
            }

            request.setRequestItems(items);

            softwareRequestService.create(request);
            logger.info("Тестовая заявка создана автоматически.");
        }
    }

    private SoftwareRequestItemEntity createRequestItem(String equipmentName, String serialNumber, String softwareName,
                                                        String softwareDescription, String softwareType, String availability) {
        SoftwareRequestItemEntity item = new SoftwareRequestItemEntity();
        item.setEquipmentName(equipmentName);
        item.setSerialNumber(serialNumber);
        item.setSoftwareName(softwareName);
        item.setSoftwareDescription(softwareDescription);
        item.setSoftwareType(softwareType);
        return item;
    }



    private List<UserEntity> createUsers() {
        List<UserEntity> users = new ArrayList<>();

        if (userService.getByEmail("afanasevstepan67@gmail.com").isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setFullName("Stepan Afanasev");
            admin.setEmail(appConfigurationProperties.getAdmin().getEmail());
            admin.setPassword(passwordEncoder.encode(appConfigurationProperties.getAdmin().getPassword()));
            admin.setPhoneNumber(appConfigurationProperties.getAdmin().getNumber());
            admin.setRole(UserRole.ADMIN);
            admin.setEmailNotificationEnabled(true);
            admin.setWebNotificationEnabled(true);

            users.add(userService.create(admin));
            logger.info("Admin user created.");
        } else {
            logger.warn("Admin user already exists. Skipping creation.");
        }

        return users;
    }
}
