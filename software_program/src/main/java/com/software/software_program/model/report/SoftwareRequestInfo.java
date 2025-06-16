package com.software.software_program.model.report;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoftwareRequestInfo {
    private String serialNumber;         // Инв. номер
    private String equipmentName;  // Описание ПК
    private String softwareName;          // Название ПО
    private String softwareDescription;   // Описание ПО
    private String softwareType;          // Тип
    private String availability;          // Наличие
}

