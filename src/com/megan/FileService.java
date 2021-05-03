package com.megan;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileService {
    public static String readContentFromFileToString(String fileName) throws IOException {
        StringBuilder fileContent = new StringBuilder();
    
        BufferedReader reader = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8));
        String line = reader.readLine();
        do {
            fileContent.append(line);
            line = reader.readLine();
        } while (line != null);
    
        reader.close();
    
        return fileContent.toString();
    }
    
    public static byte[] readContentFromFileToBytes(String fileName) throws IOException {
        File file = new File(fileName);
        return Files.readAllBytes(file.toPath());
    }
    
    public static void writeStringContentToFile(String folder, String fileName, String content) throws IOException {
        createDirectory(folder);
        
        File file = new File(folder+"/"+fileName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), StandardCharsets.UTF_8));
        writer.write(content);
        writer.close();
        System.out.println("Result saved to file "+file.getAbsolutePath());
    }
    
    public static void writeByteContentToFile(String folder, String fileName, byte[] content) throws IOException {
        createDirectory(folder);
        
        File file = new File(folder+"/"+fileName);
        try (FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile())) {
            fos.write(content);
        }
        System.out.println("Result saved to file "+file.getAbsolutePath());
    }
    
    private static void createDirectory(String folder) {
        if(folder!=null) {
            File directory = new File(folder);
            if(!directory.exists()) {
                directory.mkdir();
            }
        }
    }
}
