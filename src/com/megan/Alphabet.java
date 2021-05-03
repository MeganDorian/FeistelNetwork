package com.megan;

public class Alphabet {
    private final int[] englishMin = {65, 97};
    private final int[] englishMax = {90, 122};
    
//    private final int[] russianMin = {1040, 1025, 1046, 1105, 1078};
//    private final int[] russianMax = {1045, 1025, 1077, 1105, 1103};
    
    private final int[] specialsMin = {32, 91, 123};
    private final int[] specialsMax = {64, 96, 126};
    
    private final char[][] alphabet;
    
    public Alphabet() {
        alphabet = new char[2][getFullAlphabetSize()];
        setFullAlphabet();
    }
    
    private int getFullAlphabetSize() {
        int size =0;
        for(int i=0; i<englishMax.length; i++) {
            size+=englishMax[i]-englishMin[i]+1;
        }
//        for(int i=0; i<russianMax.length; i++) {
//            size+=russianMax[i]-russianMin[i]+1;
//        }
        for(int i=0; i<specialsMax.length; i++) {
            size+=specialsMax[i]-specialsMin[i]+1;
        }
        
        return size;
    }
    
    private int setAlphabet(int index, int min, int max) {
        for (int i = min; i <= max; i++) {
            alphabet[0][index] = (char) i;
            index++;
        }
        return index;
    }
    
    private void setFullAlphabet() {
        int j = 0;
        for(int i=0; i< englishMax.length; i++) {
            j=setAlphabet(j, englishMin[i], englishMax[i]);
        }
//        for(int i=0; i< russianMax.length; i++) {
//            j=setAlphabet(j, russianMin[i], russianMax[i]);
//        }
        for(int i=0; i< specialsMax.length; i++) {
            j=setAlphabet(j, specialsMin[i], specialsMax[i]);
        }
    }
    
    public int alphabetSize() {
        return alphabet[0].length;
    }
    
    public void setValue(int index, char value) {
        alphabet[1][index] = value;
    }
    
    public char findValueByKey(char key) {
        int size=alphabetSize();
        for (int i = 0; i < size; i++) {
            if (alphabet[0][i] == key) {
                return alphabet[1][i];
            }
        }
        
        return (char) 0;
    }
    
    public char findKeyByValue(char key) {
        int size=alphabetSize();
        for (int i = 0; i < size; i++) {
            if (alphabet[1][i] == key) {
                return alphabet[0][i];
            }
        }
        
        return (char) 0;
    }
    
    public char getCharKey(int index) {
        return alphabet[0][index];
    }
    
    public char getCharValue(int index) {
        return alphabet[1][index];
    }
}
