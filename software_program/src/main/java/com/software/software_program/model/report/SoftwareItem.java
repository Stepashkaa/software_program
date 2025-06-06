package com.software.software_program.model.report;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class SoftwareItem {
    private String name;
    private String version;
    private Date installationDate;
}
