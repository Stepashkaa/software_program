package com.software.software_program.service.log;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.core.error.FileNotFoundException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService {
    public List<String> listLogFiles() {
        File dir = new File(Constants.LOG_PATH);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalStateException("Log directory does not exist or is not a directory");
        }

        String[] logFiles = dir.list((d, name) -> name.endsWith(".log"));
        if (logFiles == null || logFiles.length == 0) {
            throw new FileNotFoundException("No log files found");
        }

        return Arrays.stream(logFiles)
                .sorted()
                .collect(Collectors.toList());
    }

    public Resource getLogFile(String filename) {
        Path path = Paths.get(Constants.LOG_PATH, filename).normalize();
        File file = path.toFile();

        if (!file.exists() || !file.isFile()
                || !file.getParentFile().getAbsolutePath().equals(new File(Constants.LOG_PATH).getAbsolutePath())) {
            throw new FileNotFoundException("Log file not found or outside allowed directory");
        }

        return new FileSystemResource(file);
    }
}
