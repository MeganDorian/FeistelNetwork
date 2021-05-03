package com.megan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Caesar {
    public final String FILE_NAME = "caesar.txt";
    public final String ENCRYPT_FOLDER = "enc";
    public final String DECRYPT_FOLDER = "dec";
    
    private StringBuilder encryptedText;
    private StringBuilder decryptedText;
    
    private final List<Integer> keys;
    private final List<Alphabet> alphabets;
    
    public Caesar(int key) {
        keys = new ArrayList<>();
        setKey(key);
        alphabets = new ArrayList<>(keys.size());
        for (Integer currentKey : keys) {
            setAlphabets(currentKey);
        }
    }
    
    private String removeNullBytesFromText(String text) {
        int textLength = text.length();
        for(int i=textLength-1; i>=0; i--) {
            if (text.charAt(i)=='\0') {
                text=text.substring(0, i);
            }
            else {
                break;
            }
        }
        return text;
    }
    
    /**
     * Получаем новый алфавит с указанным сдвигом для каждого блока текста
     * @param key сдвиг
     */
    private void setAlphabets(int key) {
        Alphabet alphabet = new Alphabet();
        int size = alphabet.alphabetSize();
        
        for(int i=0; i<size-key; i++) {
            char newValue = alphabet.getCharKey(i+key);
            alphabet.setValue(i, newValue);
        }
        int index =0;
        for(int i=size-key; i<size; i++) {
            char newValue = alphabet.getCharKey(index);
            alphabet.setValue(i, newValue);
            index++;
        }
        
        alphabets.add(alphabet);
    }
    
    /**
     * Разбиваем введенный ключ, чтобы получить сдвиг для каждого блока
     * @param key введенный ключ
     */
    private void setKey(int key) {
        while(key>0) {
            keys.add(0, key%10);
            key=key/10;
        }
        
    }
    
    private List<String> setTextBlocks(String text) {
        List<String> blocks = new ArrayList<>();
        StringBuilder stringText= new StringBuilder(removeNullBytesFromText(text));
        int size = stringText.length()/keys.size()+1;
        while(stringText.length()>=size) {
            String block = stringText.substring(0, size);
            stringText.replace(0, size, "");
            blocks.add(block);
        }
    
        if(stringText.length()!=0) {
            blocks.add(stringText.toString());
        }
        
        return blocks;
    }
    
    public void encrypt(String text) throws IOException {
        var blocks = setTextBlocks(text);
        encode(blocks);
        
        FileService.writeStringContentToFile(ENCRYPT_FOLDER, FILE_NAME, encryptedText.toString());
    }
    
    private void changeChar(int alphabetIndex, String block) {
        var alphabet = alphabets.get(alphabetIndex);
        
        for(int i=0; i<block.length(); i++) {
            char value = alphabet.findValueByKey(block.charAt(i));
            encryptedText.append(value);
        }
    }
    
    private void encode(List<String> blocks) {
        encryptedText=new StringBuilder();
        for(int i=0; i< blocks.size(); i++) {
            String block = blocks.get(i);
            changeChar(i, block);
        }
    }
    
    private void returnChar(int index, String block) {
        var alphabet = alphabets.get(index);
    
        for(int i=0; i<block.length(); i++) {
            char value = alphabet.findKeyByValue(block.charAt(i));
            decryptedText.append(value);
        }
    }
    
    private void decode(List<String> blocks) {
        decryptedText=new StringBuilder();
        for(int i=0; i< blocks.size(); i++) {
            String block = blocks.get(i);
            returnChar(i, block);
        }
    }
    
    public void decrypt(String text) throws IOException {
        var blocks = setTextBlocks(text);
        decode(blocks);
        
        FileService.writeStringContentToFile(DECRYPT_FOLDER, FILE_NAME, decryptedText.toString());
    }
}
