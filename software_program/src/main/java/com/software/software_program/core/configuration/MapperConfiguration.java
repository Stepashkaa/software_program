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

        modelMapper.typeMap(ClassroomEntity.class, ClassroomDto.class)
                .addMappings(mapper -> {
                    mapper.skip(ClassroomDto::setEquipmentNames);
                    mapper.skip(ClassroomDto::setClassroomSoftwareNames);
                });

        modelMapper.typeMap(DepartmentEntity.class, DepartmentDto.class)
                .addMappings(mapper -> {
                    mapper.skip(DepartmentDto::setClassroomNames);
                    mapper.skip(DepartmentDto::setReportNames);
                });

        modelMapper.typeMap(SoftwareEntity.class, SoftwareDto.class)
                .addMappings(mapper -> {
                    mapper.skip(SoftwareDto::setClassroomSoftwareNames);
                });

        modelMapper.typeMap(SoftwareRequestEntity.class, SoftwareRequestDto.class)
                .addMappings(mapper -> {
                    mapper.skip(SoftwareRequestDto::setUserName);
                    mapper.skip(SoftwareRequestDto::setClassroomSoftwareName);
                });

        return modelMapper;
    }
}