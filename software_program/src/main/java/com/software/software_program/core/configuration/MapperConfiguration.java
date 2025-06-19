package com.software.software_program.core.configuration;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.DepartmentEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import com.software.software_program.model.entity.SoftwareRequestEntity;
import com.software.software_program.web.dto.entity.ClassroomDto;
import com.software.software_program.web.dto.entity.DepartmentDto;
import com.software.software_program.web.dto.entity.SoftwareDto;
import com.software.software_program.web.dto.entity.SoftwareRequestDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {

    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(ClassroomEntity.class, ClassroomDto.class).addMappings(mapper -> {
            mapper.skip(ClassroomDto::setEquipmentIds);
            mapper.skip(ClassroomDto::setEquipmentNames);
        });

        modelMapper.typeMap(DepartmentEntity.class, DepartmentDto.class).addMappings(mapper -> {
            mapper.skip(DepartmentDto::setClassroomIds);
            mapper.skip(DepartmentDto::setClassroomNames);
            mapper.map(src -> src.getFaculty().getName(), DepartmentDto::setFacultyName);
            mapper.map(src -> src.getHead().getFullName(), DepartmentDto::setHeadName);
        });

        modelMapper.typeMap(SoftwareEntity.class, SoftwareDto.class).addMappings(mapper -> {
            mapper.skip(SoftwareDto::setEquipmentIds);
            mapper.skip(SoftwareDto::setEquipmentNames);
        });

        modelMapper.typeMap(SoftwareRequestEntity.class, SoftwareRequestDto.class).addMappings(mapper -> {
            mapper.map(src -> src.getUser().getFullName(), SoftwareRequestDto::setUserName);
        });

        return modelMapper;
    }

}