package com.company;

import java.util.Arrays;

class Main {
    public static void main(String[] args) {
        String[] testInputs = new String[5];
        testInputs[0] = "17+20*15";
        testInputs[1] = "35*(19-13-5)-2*3*(9-7)+(2*(17+4)-12)/5";
        testInputs[2] = "(1-(6*4-6)((5(9))";
        testInputs[3] = "(6*10)*(6+3)-7";
        testInputs[4] = "(((17)+20)*15)";

        int i = 4;

        System.out.printf("java LR1 \"%s\"\n\n", testInputs[i]);
        Parser parser = new Parser(testInputs[i] + '$');
        while (true) {
            parser.table();
            parser.print();
        }
    }
}
