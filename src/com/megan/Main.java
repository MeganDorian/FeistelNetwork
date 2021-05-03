package com.megan;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static Scanner scanner;
    
    private static String getStringFileContent(String fileName) {
        String text=null;
        try {
            text = FileService.readContentFromFileToString(fileName);
        } catch (IOException e) {
            System.err.println("Can't access file " + fileName);
            e.printStackTrace();
        }
        
        return text;
    }
    
    private static byte[] getByteFileContent(String fileName) {
        byte[] text=null;
        try {
            text = FileService.readContentFromFileToBytes(fileName);
        } catch (IOException e) {
            System.err.println("Can't access file " + fileName);
            e.printStackTrace();
        }
        
        return text;
    }
    
    private static int getIntegerKey() {
        int key;
        while (true) {
            try {
                System.out.print("Enter Caesar key value: ");
                key = scanner.nextInt();
                break;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.err.println("Enter integer key value please");
            }
        }
        return key;
    }
    
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        String command;
        int key;
        String text;
        byte[] byteText;
        Caesar caesar;
        Feistel feistel;
        String feistelKey=null;
        System.out.println("Available commands: enc, dec, exit");
        do {
            command = scanner.nextLine();
            String[] params = command.split(" ");
            switch (params[0]) {
                case "enc": {
                    text = getStringFileContent(params[1]);
                    if (text != null && !text.isEmpty()) {
                        key = getIntegerKey();
                        caesar = new Caesar(key);
                        try {
                            caesar.encrypt(text);
    
                            byteText = getByteFileContent(caesar.ENCRYPT_FOLDER + "/" + caesar.FILE_NAME);
                            if (byteText != null && byteText.length!=0) {
                                feistel = new Feistel(byteText);
                                feistel.setKey();
                                feistelKey=feistel.getKey();
                                System.out.println("Key for Feistel Network: "+feistelKey);
                                feistel.encrypt();
                            }
    
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
    
                    break;
                }
                case "dec": {
                    byteText = getByteFileContent(params[1]);
    
                    if (byteText != null && byteText.length!=0) {
                        try {
                            feistel = new Feistel(byteText);
                            feistel.decrypt(feistelKey);
    
                            text = getStringFileContent(feistel.DECRYPT_FOLDER + "/" + feistel.FILE_NAME);
                            if (text != null && !text.isEmpty()) {
                                key = getIntegerKey();
                                caesar = new Caesar(key);
                                caesar.decrypt(text);
                            }
    
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case "exit":
                    return;
                default:
                    System.err.println("No command found");
                    break;
            }
            scanner.nextLine();
        } while (!command.equals("exit"));
        
    }
}
