package com.software.software_program.web.controller.log;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.service.log.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/log")
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    @GetMapping
    public List<String> getLogFiles(@RequestParam(required = false) String search) {
        return logService.listLogFiles(search);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> downloadLogFile(@PathVariable String filename) {
        Resource resource = logService.getLogFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }
}
