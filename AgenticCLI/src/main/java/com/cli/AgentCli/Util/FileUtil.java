package com.cli.AgentCli.Util;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileUtil {
    public String readFile(String path){
        try {
            String normalizedPath = path.replace("\\", "/");
            return Files.readString(Path.of(normalizedPath));
        } catch (Exception e) {
            System.err.println("FileUtil ERROR: " + e.getMessage());
            return null;
        }
    }
}
