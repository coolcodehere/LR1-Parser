package com.company;

import java.util.Stack;

public class Parser {
    int state = 0;
    int currentInputIdx = 0;
    String[] input;
    Stack<StackItem> symbols = new Stack<>();

    public Parser(String input) {
        this.input = splitInput(input);
        symbols.push(new StackItem('✷', 0));
    }

    private String[] splitInput(String input) {
        return input.split("(?<=[-+*/()$])|(?=[-+*/()$])");
    }

    public void table() {
        String cInput = input[currentInputIdx];
        if (isInt(cInput)) {
            cInput = "n";
        }

        switch(cInput) {
            case "+":
                addSub('+');
                break;
            case "-":
                addSub('-');
                break;
            case "*":
                multDiv('*');
                break;
            case "/":
                multDiv('/');
                break;
            case "(":
                openParen();
                break;
            case ")":
                closeParen();
                break;
            case "$":
                lambda();
                break;
            case "n":
                n();
                break;
            default:
                System.out.println("\nBAD INPUT");
                System.exit(0);
        }
    }

    public boolean isInt(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public void n() {
        switch(state) {
            case 0:
            case 4:
            case 6:
            case 7:
                shift('n', 5, Integer.parseInt(input[currentInputIdx++]));
                break;
            default:
                System.out.println("\nBAD INPUT");
                System.exit(0);
        }
    }

    public void addSub(char operator) {
        switch(state) {
            case 1:
            case 8:
                shift(operator, 6);
                currentInputIdx++;
                break;
            case 2:
                reduce('E', "T");
                symbols.peek().state = E(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 3:
                reduce('T', "F");
                symbols.peek().state = T(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 5:
                reduce('F', "n");
                symbols.peek().state = F(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 9:
                reduce('E', "E+T", "E-T");
                symbols.peek().state = E(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 10:
                reduce('T', "T*F", "T/F");
                symbols.peek().state = T(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 11:
                reduce('F', "(E)");
                symbols.peek().state = F(peekSecond().state);
                state = symbols.peek().state;
                break;
            default:
                System.out.println("\nBAD INPUT");
                System.exit(0);
        }
    }

    public void multDiv(char operator) {
        switch(state) {
            case 2:
            case 9:
                shift(operator, 7);
                currentInputIdx++;
                break;
            case 3:
                reduce('T', "F");
                symbols.peek().state = T(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 5:
                reduce('F', "n");
                symbols.peek().state = F(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 10:
                reduce('T', "T*F", "T/F");
                symbols.peek().state = T(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 11:
                reduce('F', "(E)");
                symbols.peek().state = F(peekSecond().state);
                state = symbols.peek().state;
                break;
            default:
                System.out.println("\nBAD INPUT");
                System.exit(0);
        }
    }

    public void openParen() {
        switch(state) {
            case 0:
            case 4:
            case 6:
            case 7:
                shift('(', 4);
                currentInputIdx++;
                break;
            default:
                System.out.println("\nBAD INPUT");
                System.exit(0);
        }
    }

    public void closeParen() {
        switch(state) {
            case 2:
                reduce('E', "T");
                symbols.peek().state = E(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 3:
                reduce('T', "F");
                symbols.peek().state = T(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 5:
                reduce('F', "n");
                symbols.peek().state = F(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 8:
                shift(')', 11);
                currentInputIdx++;
                break;
            case 9:
                reduce('E', "E+T", "E-T");
                symbols.peek().state = E(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 10:
                reduce('T', "T*F", "T/F");
                symbols.peek().state = T(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 11:
                reduce('F', "(E)");
                symbols.peek().state = F(peekSecond().state);
                state = symbols.peek().state;
                break;
            default:
                System.out.println("\nBAD INPUT");
                System.exit(0);
        }
    }

    public int E(int nextState) {
        switch(nextState) {
            case 0:
                return 1;
            case 4:
                return 8;
            default:
                System.out.println("\nBAD INPUT");
                System.exit(0);
                return -1;
        }
    }

    public int T(int nextState) {
        switch(nextState) {
            case 0:
            case 4:
                return 2;
            case 6:
                return 9;
            default:
                System.out.println("\nBAD INPUT");
                System.exit(0);
                return -1;
        }
    }

    public int F(int nextState) {
        switch(nextState) {
            case 0:
            case 4:
            case 6:
                return 3;
            case 7:
                return 10;
            default:
                System.out.println("\nBAD INPUT");
                System.exit(0);
                return -1;
        }
    }

    public void lambda() {
        switch(state) {
            case 1:
                System.out.printf("\nValid Expression, value = %d", symbols.peek().value);
                System.exit(0);
                break;
            case 2:
                reduce('E', "T");
                symbols.peek().state = E(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 3:
                reduce('T', "F");
                symbols.peek().state = T(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 5:
                reduce('F', "n");
                symbols.peek().state = F(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 9:
                reduce('E', "E+T", "E-T");
                symbols.peek().state = E(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 10:
                reduce('T', "T*F", "T/F");
                symbols.peek().state = T(peekSecond().state);
                state = symbols.peek().state;
                break;
            case 11:
                reduce('F', "(E)");
                symbols.peek().state = F(peekSecond().state);
                state = symbols.peek().state;
                break;
            default:
                System.out.println("\nBAD INPUT");
                System.exit(0);
        }
    }

    private void shift(char symbol, int state, int value) {
        symbols.push(new StackItem(symbol, state, value));
        this.state = state;
    }

    private void shift(char symbol, int state) {
        symbols.push(new StackItem(symbol, state));
        this.state = state;
    }

    private void reduce(char reduceTo, String reduceFrom) {
        String currentSymbols = "";
        int val;
        StackItem[] items;

        if (reduceFrom.equals("(E)")) {
            items = new StackItem[3];
            items[0] = symbols.pop();
            items[1] = symbols.pop();
            items[2] = symbols.pop();
            val = items[1].value;
        } else if (reduceFrom.length() == 3) {
            items = new StackItem[3];
            items[0] = symbols.pop();
            items[1] = symbols.pop();
            items[2] = symbols.pop();
            val = combineValues(items[0].value, items[2].value, items[1].symbol);
        } else {
            items = new StackItem[1];
            items[0] = symbols.pop();
            val = items[0].value;
        }

        for (int i = 0; i < items.length; i++) {
            currentSymbols += items[i].symbol;
        }

        currentSymbols = new StringBuilder(currentSymbols).reverse().toString();

        if (reduceFrom.equals(currentSymbols)) {
            symbols.push(new StackItem(reduceTo, 0, val));
        } else {
            System.out.println("[reduce] reduceFrom doesn't match stack contents!\nreduceFrom: " + reduceFrom + "\ncurrentSymbols: "+ currentSymbols + "\n");
            System.exit(0);
        }
    }

    private void reduce(char reduceTo, String reduceFromFirst, String reduceFromSecond) {
        String currentSymbols = "";
        int val;
        StackItem[] items = new StackItem[3];
        items[0] = symbols.pop();
        items[1] = symbols.pop();
        items[2] = symbols.pop();
        val = combineValues(items[0].value, items[2].value, items[1].symbol);


        for (int i = 0; i < items.length; i++) {
            currentSymbols += items[i].symbol;
        }

        currentSymbols = new StringBuilder(currentSymbols).reverse().toString();

        if (reduceFromFirst.equals(currentSymbols) || reduceFromSecond.equals(currentSymbols)) {
            symbols.push(new StackItem(reduceTo, 0, val));
        } else {
            System.out.println("[reduce] reduceFrom doesn't match stack contents!\nreduceFrom: " + reduceFromFirst + " or " + reduceFromSecond + "\ncurrentSymbols: " + currentSymbols + "\n");
            System.exit(0);
        }
    }

    private int combineValues(int val1, int val2, char operator) {
        switch (operator) {
            case '+':
                return val1 + val2;
            case '-':
                return val2 - val1;
            case '*':
                return val1 * val2;
            case '/':
                return val2 / val1;

            default:
                System.out.println("No operator given!");
                System.exit(-1);
        }
        return 0;
    }

    private StackItem peekSecond() {
        StackItem ret;
        StackItem top = symbols.pop();
        StackItem second = symbols.peek();
        symbols.push(top);
        return second;
    }

    public void print() {
        String inputQ = "[";
        for (int i = currentInputIdx; i < input.length; i++) {
            inputQ += input[i];
        }
        System.out.printf("Stack:%s\tInput Queue:%s]\n", symbols, inputQ);
    }
}

class StackItem {
    char symbol;
    int value;
    int state;
    boolean hasValue;

    public StackItem(char symbol, int state) {
        this.symbol = symbol;
        this.state = state;
        hasValue = false;
    }

    public StackItem(char symbol, int state, int value) {
        this.symbol = symbol;
        this.value = value;
        this.state = state;
        hasValue = true;
    }

    public String toString() {
        if (hasValue) {
            return "("+symbol+"="+value+":"+state+")";
        } else {
            return "("+symbol+":"+state+")";
        }
    }

    public void setValue(int value) {
        hasValue = true;
        this.value = value;
    }
}