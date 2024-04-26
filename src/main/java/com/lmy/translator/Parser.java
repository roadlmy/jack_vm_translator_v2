package com.lmy.translator;

import java.io.*;
import java.util.HashMap;

public class Parser {

    private static HashMap<String,String> commandTypeMap = new HashMap<>();
    static {
        commandTypeMap.put("push","C_PUSH");
        commandTypeMap.put("pop","C_POP");
        commandTypeMap.put("add","C_ARITHMETIC");
        commandTypeMap.put("sub","C_ARITHMETIC");
        commandTypeMap.put("neg","C_ARITHMETIC");
        commandTypeMap.put("eq","C_ARITHMETIC");
        commandTypeMap.put("gt","C_ARITHMETIC");
        commandTypeMap.put("lt","C_ARITHMETIC");
        commandTypeMap.put("and","C_ARITHMETIC");
        commandTypeMap.put("or","C_ARITHMETIC");
        commandTypeMap.put("not","C_ARITHMETIC");
        commandTypeMap.put("goto","C_GOTO");
        commandTypeMap.put("if-goto","C_IF");
        commandTypeMap.put("label","C_LABEL");
        commandTypeMap.put("call","C_CALL");
        commandTypeMap.put("function","C_FUNCTION");
        commandTypeMap.put("return","C_RETURN");
    }
    File f;
    BufferedReader bufferedReader;

    String thisLineVmCode;
    public Parser(File f) throws FileNotFoundException {
        this.f = f;
        this.bufferedReader =new BufferedReader(new InputStreamReader(new FileInputStream(f)));
    }

    public Parser() {
    }

    public boolean hasMoreCommand() throws IOException {
        thisLineVmCode = bufferedReader.readLine();
        boolean hasMoreCommand = (thisLineVmCode != null);
        if(hasMoreCommand){
            thisLineVmCode = thisLineVmCode.trim();
        }
        return hasMoreCommand;
    }

    public String advance(){
        return thisLineVmCode; // 当前仅当hasMoreCommand为真才能执行该函数
    }

    public String commandType() throws Exception {
        String commandType = null;
        if(thisLineVmCode != null){
            String[] s = thisLineVmCode.split(" ");
            commandType = commandTypeMap.get(s[0]);
            if(commandType == null){
                throw new Exception("不存在这种vm指令，请检查");
            }

        }
        return commandType;
    }

    public String arg0(){
        if(thisLineVmCode != null){
            String[] s = thisLineVmCode.split(" ");
            if(s.length==1){
                return s[0];
            }else if(s.length==2){
                return s[1];

            }else if(s.length==3){
                return s[1];
            }
        }

        return null;
    }

    public Integer arg1(){
        if(thisLineVmCode != null){
            String[] s = thisLineVmCode.split(" ");
            if(s.length==1){
                return null;
            }else if(s.length==2){
                return null;
            }else if(s.length==3){
                return Integer.parseInt(s[2]);
            }
        }

        return null;
    }

    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        parser.setThisLineVmCode("push pointer 3");
        System.out.println(parser.arg0());
        System.out.println(parser.arg1());
        System.out.println(parser.advance());

        parser.setThisLineVmCode(null);
        System.out.println(parser.arg0());
        System.out.println(parser.arg1());
        System.out.println(parser.advance());

        parser.setThisLineVmCode("add");
        System.out.println(parser.arg0());
        System.out.println(parser.arg1());
        System.out.println(parser.advance());

    }

    public File getF() {
        return f;
    }

    public void setF(File f) {
        this.f = f;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public String getThisLineVmCode() {
        return thisLineVmCode;
    }

    public void setThisLineVmCode(String thisLineVmCode) {
        this.thisLineVmCode = thisLineVmCode;
    }

    public void close() throws IOException {
        bufferedReader.close();
    }
}
