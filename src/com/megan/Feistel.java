package com.megan;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Feistel {
    public final String FILE_NAME = "feistel.txt";
    public final String ENCRYPT_FOLDER = "enc";
    public final String DECRYPT_FOLDER = "dec";
    
    private final int blockSize = 16;
    private final int streamSize = 4;
    private final int roundCount = 8;
    
    private byte[] key;
    private byte[] text;
    
    private byte[] stream1;
    private byte[] stream2;
    private byte[] stream3;
    private byte[] stream4;
    
    public Feistel(byte[] text) {
        this.text=text;
    }
    
    private int setResultTextSize() {
        int size=text.length;
        if(text.length % 16 != 0) {
            int neededBlockCount = text.length/blockSize+1;
            size=neededBlockCount*blockSize;
        }
        return size;
    }
    
    public void setKey() {
        key = new byte[streamSize];
        int min = 33;
        int max = 126;
        Random random = new Random();
        for(int i=0; i<key.length; i++) {
            key[i]=(byte)(random.nextInt(max-min)+min);
        }
    }
    
    public String getKey() {
        return new String(key);
    }
    
    private void changeKeyTo() {
        byte temp = key[0];
        key[0]= (byte)(key[3]^streamSize);
        key[3]=(byte)(key[1]^streamSize);
        key[1]=key[2];
        key[2]=temp;
    }
    
    private void changeKeyFrom() {
        byte temp = key[2];
        key[2]=key[1];
        key[1]=(byte)(key[3]^streamSize);
        key[3]= (byte)(key[0]^streamSize);
        key[0]=temp;
    }
    
    private void setBlocks(byte[] block) {
        stream1 = Arrays.copyOfRange(block, 0, streamSize);
        stream2 = Arrays.copyOfRange(block, streamSize, streamSize*2);
        stream3 = Arrays.copyOfRange(block, streamSize*2, streamSize*3);
        stream4 = Arrays.copyOfRange(block, streamSize*3, streamSize*4);
    }
    
    private void sout() {
        System.out.print("stream1:\t");
        for(int i=0; i<4; i++) {
            String bin = String.format("%8s\t", Integer.toBinaryString(stream1[i])).replace(' ', '0');
            System.out.print(bin);
        }
        System.out.println("\t"+new String(stream1));
        System.out.print("stream2:\t");
        for(int i=0; i<4; i++) {
            String bin = String.format("%8s\t", Integer.toBinaryString(stream2[i])).replace(' ', '0');
            System.out.print(bin);
        }
        System.out.println("\t"+new String(stream2));
        System.out.print("stream3:\t");
        for(int i=0; i<4; i++) {
            String bin = String.format("%8s\t", Integer.toBinaryString(stream3[i])).replace(' ', '0');
            System.out.print(bin);
        }
        System.out.println("\t"+new String(stream3));
        System.out.print("stream4:\t");
        for(int i=0; i<4; i++) {
            String bin = String.format("%8s\t", Integer.toBinaryString(stream4[i])).replace(' ', '0');
            System.out.print(bin);
        }
        System.out.println("\t"+new String(stream4));
    }
    
    private void soutKey() {
        System.out.print("currKey:\t");
        for(int i=0; i<4; i++) {
            String bin = String.format("%8s\t", Integer.toBinaryString(key[i])).replace(' ', '0');
            System.out.print(bin);
        }
        System.out.println("\t"+new String(key));
    }
    
    private void removeFromByteArray() {
        StringBuilder originalText = new StringBuilder(new String(text));
        originalText.replace(0, blockSize, "");
        text = originalText.toString().getBytes();
    }
    
    public void encrypt() throws IOException {
        int steps = (int)Math.ceil((double)text.length / blockSize);
    
        byte[] encryptedText = new byte[setResultTextSize()];
        int j=0;
        while (j<steps) {
            if(text.length<blockSize) {
                addEmptyBytes();
            }
            setBlocks(Arrays.copyOfRange(text, 0, blockSize));
//            System.out.println("\nУстановленные блоки:");
//            sout();
            
            for(int i=0; i<roundCount; i++) {
                roundTo();
                addToResultText(encryptedText, j);
                changeKeyTo();
            }
            
            byte[] temp = stream1;
            stream1=stream4;
            stream4=stream3;
            stream3=stream2;
            stream2=temp;
            addToResultText(encryptedText, j);
    
//            System.out.println("\nФинальное перемешивание:");
//            sout();
//            System.out.println("\nКонечный ключ:");
//            soutKey();
            
            removeFromByteArray();
            j++;
        }
        
        FileService.writeByteContentToFile(ENCRYPT_FOLDER, FILE_NAME, encryptedText);
    }
    
    private void setAndCalculateKey(String key) {
        this.key = new byte[streamSize];
        for(int i=0; i<streamSize; i++) {
            this.key[i]=(byte)key.charAt(i);
        }
        
        for(int i=0; i< roundCount; i++) {
            changeKeyTo();
        }
    }
    
    public void decrypt(String key) throws IOException {
        setAndCalculateKey(key);
    
        int steps = (int)Math.ceil((double)text.length / blockSize);
    
        byte[] decryptedText = new byte[setResultTextSize()];
        int j=0;
        while (j<steps) {
            if(text.length<blockSize) {
                addEmptyBytes();
            }
            setBlocks(Arrays.copyOfRange(text, 0, blockSize));
//            System.out.println("\nУстановленные блоки:");
//            sout();
            
            byte[] temp = stream1;
            stream1=stream2;
            stream2=stream3;
            stream3=stream4;
            stream4=temp;
            addToResultText(decryptedText, j);
            
//            System.out.println("\nНачальное перемешивание:");
//            sout();
//            System.out.println("\nИзначальный ключ:");
//            soutKey();
            
            for(int i=0; i<roundCount; i++) {
                changeKeyFrom();
                roundFrom();
                addToResultText(decryptedText, j);
            }
        
            removeFromByteArray();
            j++;
        }
        
        FileService.writeByteContentToFile(DECRYPT_FOLDER, FILE_NAME, decryptedText);
    }
    
//    private void removeNullBytesFromArray() {
//        List<Byte> withoutNulls = new ArrayList<>();
//        byte[] result = withoutNulls.toArray();
//
//    }
    
    private void addToResultText(byte[] dest, int step) {
        System.arraycopy(stream1, 0, dest, step*blockSize, stream1.length);
        System.arraycopy(stream2, 0, dest, step*blockSize+streamSize, stream2.length);
        System.arraycopy(stream3, 0, dest, step*blockSize+streamSize*2, stream3.length);
        System.arraycopy(stream4, 0, dest, step*blockSize+streamSize*3, stream4.length);
    }
    
    private void roundTo() {
        byte[] functionResult =XOR(XOR(XOR(stream1, stream2), stream3), key);
        byte[] temp = stream1;
        stream1=stream2;
        stream2=stream3;
        stream3=XOR(functionResult, stream4);
        stream4=temp;
    
//        System.out.println("\nБлоки перемешаны с ключом:");
//        soutKey();
//        System.out.println("\nПеремешанные блоки:");
//        sout();
    }
    
    private void roundFrom() {
        byte[] functionResult = XOR(XOR(XOR(stream1, stream2), stream4), key);
        byte[] end = XOR(functionResult, stream3);
        stream3=stream2;
        stream2=stream1;
        stream1=stream4;
        stream4=end;
    
//        System.out.println("\nБлоки перемешаны с ключом:");
//        soutKey();
//        System.out.println("\nПеремешанные блоки:");
//        sout();
    }
    
    private byte[] XOR(byte[] first, byte[] second) {
        byte[] result = new byte[streamSize];
        for(int i=0; i<streamSize; i++) {
            result[i]= (byte) (first[i]^second[i]);
        }
        
        return result;
    }
    
    private void addEmptyBytes() {
        int newBytesCount = blockSize-text.length;
        byte[] newBytes = new byte[newBytesCount];
        Arrays.fill(newBytes, (byte)'\0');
        byte[] newText = Arrays.copyOf(text, blockSize);
        System.arraycopy(newBytes, 0, newText, text.length, newBytes.length);
        text=newText;
    }
}
