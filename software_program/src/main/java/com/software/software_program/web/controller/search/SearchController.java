//package com.software.software_program.web.controller.search;
//
//import com.software.software_program.core.configuration.Constants;
//import com.software.software_program.model.enums.DeviceSortType;
//import com.software.software_program.service.entity.DeviceService;
//import com.software.software_program.service.entity.DeviceTypeService;
//import com.software.software_program.service.entity.ManufacturerService;
//import com.software.software_program.web.dto.search.SearchDto;
//import com.software.software_program.web.mapper.entity.DeviceMapper;
//import com.software.software_program.web.mapper.entity.DeviceTypeMapper;
//import com.software.software_program.web.mapper.entity.ManufacturerMapper;
//import com.software.software_program.web.mapper.pagination.PageDtoMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping(Constants.API_URL + "/search")
//@RequiredArgsConstructor
//public class SearchController {
//    private final DeviceService deviceService;
//    private final DeviceTypeService deviceTypeService;
//    private final ManufacturerService manufacturerService;
//    private final DeviceMapper deviceMapper;
//    private final DeviceTypeMapper deviceTypeMapper;
//    private final ManufacturerMapper manufacturerMapper;
//
//    @GetMapping
//    public SearchDto search(@RequestParam(name = "searchInfo", defaultValue = "") String searchInfo) {
//        SearchDto searchDto = new SearchDto();
//        searchDto.setDevices(PageDtoMapper.toDto(
//                deviceService.getAllByFilters(
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        searchInfo,
//                        DeviceSortType.PURCHASE_DATE_DESC,
//                        0,
//                        Integer.parseInt(Constants.DEFAULT_PAGE_SIZE)
//                ),
//                deviceMapper::toDto
//        ));
//        searchDto.setDeviceTypes(deviceTypeService.getAll(searchInfo).stream()
//                .map(deviceTypeMapper::toDto)
//                .toList());
//        searchDto.setManufacturers(manufacturerService.getAll(searchInfo).stream()
//                .map(manufacturerMapper::toDto)
//                .toList());
//        return searchDto;
//    }
//}
