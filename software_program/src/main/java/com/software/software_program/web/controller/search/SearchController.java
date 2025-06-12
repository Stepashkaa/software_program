//package com.software.software_program.web.controller.search;
//
//import com.software.software_program.core.configuration.Constants;
//import com.software.software_program.web.dto.entity.ClassroomDto;
//import com.software.software_program.web.dto.entity.DepartmentDto;
//import com.software.software_program.web.dto.entity.FacultyDto;
//import com.software.software_program.web.dto.entity.SoftwareDto;
//import com.software.software_program.service.entity.ClassroomService;
//import com.software.software_program.service.entity.DepartmentService;
//import com.software.software_program.service.entity.FacultyService;
//import com.software.software_program.service.entity.SoftwareService;
//import com.software.software_program.web.dto.search.SearchDto;
//import com.software.software_program.web.mapper.entity.ClassroomMapper;
//import com.software.software_program.web.mapper.entity.DepartmentMapper;
//import com.software.software_program.web.mapper.entity.FacultyMapper;
//import com.software.software_program.web.mapper.entity.SoftwareMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping(Constants.API_URL + "/search")
//@RequiredArgsConstructor
//public class SearchController {
//
//    private final ClassroomService classroomService;
//    private final DepartmentService departmentService;
//    private final FacultyService facultyService;
//    private final SoftwareService softwareService;
//
//    private final ClassroomMapper classroomMapper;
//    private final DepartmentMapper departmentMapper;
//    private final FacultyMapper facultyMapper;
//    private final SoftwareMapper softwareMapper;
//
//    @GetMapping
//    public SearchDto search(
//            @RequestParam(name = "searchInfo", defaultValue = "") String searchInfo,
//            Pageable pageable
//    ) {
//        SearchDto searchDto = new SearchDto();
//
//        // Поиск аудиторий
//        Page<ClassroomDto> classrooms = classroomService.getAllByFilters(searchInfo, null, null, pageable)
//                .map(classroomMapper::toDto);
//        searchDto.setClassrooms(classrooms);
//
//        // Поиск кафедр
//        Page<DepartmentDto> departments = departmentService.getAll(searchInfo, pageable)
//                .map(departmentMapper::toDto);
//        searchDto.setDepartments(departments);
//
//        // Поиск факультетов
//        Page<FacultyDto> faculties = facultyService.getAll(searchInfo, pageable)
//                .map(facultyMapper::toDto);
//        searchDto.setFaculties(faculties);
//
//        // Поиск программного обеспечения
////        Page<SoftwareDto> software = softwareService.getAllByFilters(searchInfo, null, null, pageable)
////                .map(softwareMapper::toDto);
////        searchDto.setSoftware(software);
//
//        return searchDto;
//    }
//
//    @GetMapping("/search")
//    public List<SoftwareDto> searchByName(@RequestParam String name) {
//        return softwareService.getAll(name).stream()
//                .map(softwareMapper::toDto)
//                .toList();
//    }
//
//}