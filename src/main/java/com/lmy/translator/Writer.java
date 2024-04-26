package com.lmy.translator;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class Writer {
    private static HashMap<String,String> segmentMap = new HashMap<>();
    static {
        segmentMap.put("local","LCL");
        segmentMap.put("argument","ARG");
        segmentMap.put("this","THIS");
        segmentMap.put("that","THAT");
        segmentMap.put("constant","CONSTANT");
        segmentMap.put("pointer","POINTER");
        segmentMap.put("temp","TEMP");
        segmentMap.put("static","STATIC");
    }

    private static HashMap<String,Integer> returnMap = new HashMap<>();
    File fileOut;
    BufferedWriter bw;
    BufferedReader bufferedReader;

    public static Integer EQ_Count = 0;
    public static Integer GT_Count = 0;
    public static Integer LT_Count = 0;
    public Writer(File fileOut) throws IOException {
        this.fileOut = fileOut;
        this.bw = new BufferedWriter(new FileWriter(fileOut));
    }

    public void writeArithmetic(String nowExecCode) throws IOException {
        String finalHackCode = null;
        if(Objects.equals(nowExecCode, "add")){
            finalHackCode = "// " + nowExecCode + "\n" +
                    "@SP\n" +
                    "AM=M-1 \n" + // pop y 并且 SP--
                    "D=M   \n" + // 把y存到寄存器D
                    "A=A-1 \n" +
                    "M=M+D \n";
            bw.write(finalHackCode);
        }else if(Objects.equals(nowExecCode, "sub")){
            finalHackCode = "// " + nowExecCode + "\n" +
                    "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "M=M-D\n";
            bw.write(finalHackCode);
        }else if(Objects.equals(nowExecCode, "neg")){
            finalHackCode = "// " + nowExecCode + "\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=-M\n";
            bw.write(finalHackCode);
        }else if(Objects.equals(nowExecCode, "eq")){
            finalHackCode = "// " + nowExecCode + "\n" +
                    "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +  // D = y
                    "A=A-1\n" +
                    "D=M-D\n" + // M = x -> D = x - y
                    "M=-1\n" +
                    "@EQ_LABELCOUNT_" +EQ_Count + "\n" +
                    "D;JEQ\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=0\n" +
                    "(EQ_LABELCOUNT_" + EQ_Count +")" +"\n";
            EQ_Count ++;
            bw.write(finalHackCode);
        }else if(Objects.equals(nowExecCode, "gt")){
            finalHackCode = "// " + nowExecCode + "\n" +
                    "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "D=M-D\n" +
                    "M=-1\n" +
                    "@GT_LABELCOUNT_" +GT_Count + "\n" +
                    "D;JGT\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=0\n" +
                    "(GT_LABELCOUNT_" + GT_Count +")" +"\n";
            GT_Count++;
            bw.write(finalHackCode);
        }else if(Objects.equals(nowExecCode, "lt")){
            finalHackCode = "// " + nowExecCode + "\n" +
                    "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "D=M-D\n" +
                    "M=-1\n" +
                    "@LT_LABELCOUNT_" +LT_Count + "\n" +
                    "D;JLT\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=0\n" +
                    "(LT_LABELCOUNT_" + LT_Count +")" +"\n";
            LT_Count++;
            bw.write(finalHackCode);
        }else if(Objects.equals(nowExecCode, "and")){
            finalHackCode = "// " + nowExecCode + "\n" +
                    "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "M=M&D\n";
            bw.write(finalHackCode);
        }else if(Objects.equals(nowExecCode, "or")){
            finalHackCode = "// " + nowExecCode + "\n" +
                    "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "M=M|D\n";
            bw.write(finalHackCode);
        }else if(Objects.equals(nowExecCode, "not")){
            finalHackCode = "// " + nowExecCode + "\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=!M\n";
            bw.write(finalHackCode);
        }

    }

    public void writePushPop(String commandType,String nowExecCode,String segment, Integer offset, File f) throws Exception {
        String segmentShortCut = segmentMap.get(segment);
        String file = null;
        String prefix = null;
        if(Objects.equals(commandType, "C_PUSH")){
            prefix = "push";
        } else if(Objects.equals(commandType, "C_POP")){
            prefix = "pop";
        } else {
            throw new Exception("push pop 入参数操作错误");
        }

        String fileName = this.getClass().getClassLoader().getResource("").getPath();
        if(Objects.equals(segmentShortCut, "LCL") || Objects.equals(segmentShortCut, "ARG") || Objects.equals(segmentShortCut, "THIS") || Objects.equals(segmentShortCut, "THAT")){
            file = fileName+ "asm/segment_"+prefix+".asm";
        }else if(Objects.equals(segmentShortCut, "CONSTANT")){
            file = fileName+ "asm/constant_"+prefix+".asm";
        }else if(Objects.equals(segmentShortCut, "POINTER")){
            file = fileName+ "asm/pointer_"+prefix+".asm";
        }else if(Objects.equals(segmentShortCut, "TEMP")){
            file = fileName+ "asm/temp_"+prefix+".asm";
        }else if(Objects.equals(segmentShortCut, "STATIC")){
            file = fileName+ "asm/static_"+prefix+".asm";
        }

        if(file != null){
            extracted(nowExecCode,segment, offset, file, segmentShortCut, f);
        }else {
            throw new Exception(nowExecCode + ":该条pop和push指令有误");
        }
    }

    private void extracted(String nowExecCode, String segment, Integer offset, String file, String segmentShortCut, File f) throws IOException {
        String inputFileName = f.getName().substring(f.getName().lastIndexOf("/")+1,f.getName().indexOf("."));
        FileInputStream inputStream = new FileInputStream(file);
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str = null;
        bw.write("// " + nowExecCode + "\n");
        while ((str = this.bufferedReader.readLine()) != null){
            if(str.trim().contains("SEGMENT")){
                str = str.replace("SEGMENT", segmentShortCut);
            }
            if(str.trim().contains("arg0")){
                str = str.replace("arg0", segment);
            }
            if(str.trim().contains("arg1")){
                str = str.replace("arg1", offset.toString());
            }
            if(str.trim().contains("Filename")){
                str = str.replace("Filename", file.substring(file.lastIndexOf("/")+1,file.indexOf(".")));
                str = str.replace("x", offset.toString());
            }
            if(str.trim().contains("staticLabel")){
                str = str.replace("staticLabel", inputFileName + "_" + offset.toString());
            }
            bw.write(str + "\n");
        }
        bufferedReader.close();
    }

    public void close() throws IOException {
        bw.close();
    }

    public void writeGoto(String s) throws IOException {
        String label = s.substring(s.lastIndexOf(" ")+1);
        bw.write("// goto" + s + "\n");
        bw.write("@" + label + "\n");
        bw.write("0;JMP\n");
    }

    public void writeInit() throws Exception {
        String fileName = this.getClass().getClassLoader().getResource("").getPath() + "init/init.asm";
        FileInputStream inputStream = new FileInputStream(fileName);
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str = null;
        Integer sysCount = returnMap.getOrDefault("Sys",0);
        sysCount ++;
        returnMap.put("Sys",sysCount);
        bw.write("// SP = 256\n");
        bw.write("// call Sys.init\n");
        while ((str = this.bufferedReader.readLine()) != null){
            str = str.trim();
            if(str.startsWith("//") || str.isEmpty()){
                continue;
            }
            if(str.contains("functionName")){
                str = str.replace("functionName", "Sys.init");
            }
            if(str.contains("rretAddrLabel")){
                str = str.replace("rretAddrLabel", "Sys$ret."+sysCount);
            }
            bw.write(str + "\n");
        }
        bufferedReader.close();
    }

    public void writeIf(String s) throws IOException {
        String label = s.substring(s.lastIndexOf(" ")+1);
        bw.write("// if-goto " + s + "\n");
        bw.write("@SP\n");
        bw.write("AM=M-1\n");
        bw.write("D=M\n");
//        bw.write("A=A-1\n"); // 这个只动了A寄存器让他可以访问SP--，但并没有改动SP指针本身的值！！（要改动SP必须要改M）


        bw.write("@" + label + "\n");
        bw.write("D;JNE\n");
    }

    public void writeLabel(String s) throws IOException {
        bw.write("// label " + s + "\n");
        bw.write("(" +s + ")"+"\n");
    }

    public void writeFunction(String functionName, Integer nLocalVars) throws IOException {
        bw.write("// function " + functionName + " " + nLocalVars + "\n");

        bw.write("("+functionName +")\n");
        String fileName = this.getClass().getClassLoader().getResource("").getPath() + "func/init_local0.asm";
        FileInputStream inputStream = new FileInputStream(fileName);
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder localCode = new StringBuilder();
        String str = null;
        StringBuilder finalCode = new StringBuilder();

        while ((str = this.bufferedReader.readLine()) != null){
            str = str.trim();
            if(str.startsWith("//") || str.isEmpty()){
                continue;
            }
            localCode.append(str).append("\n");
        }
        bufferedReader.close();
        for(; nLocalVars != 0; nLocalVars--){
            finalCode.append(localCode);
        }

         bw.write(String.valueOf(finalCode));

    }

    public void writeCall(String functionName, Integer nArgVars) throws IOException {
        String fileName = this.getClass().getClassLoader().getResource("").getPath() + "func/call.asm";
        FileInputStream inputStream = new FileInputStream(fileName);
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str = null;
        String replacement = functionName.substring(0,functionName.lastIndexOf("."));
        Integer i = returnMap.get(replacement);
        if(i == null){
            i = 1;
        }else {
            i ++;
        }
        returnMap.put(replacement,i);
        bw.write("// call "+ functionName + " " +nArgVars + "\n");
        while ((str = this.bufferedReader.readLine()) != null){
            str = str.trim();
            if(str.startsWith("//") || str.isEmpty()){
                continue;
            }
            if(str.contains("functionName")){
                str = str.replace("functionName", functionName);
            }
            if(str.contains("nArgs")){
                str = str.replace("nArgs", String.valueOf(nArgVars));
            }
            if(str.contains("retAddrLabel")){
                str = str.replace("retAddrLabel",  replacement + "$ret."+i);
            }
            bw.write(str + "\n");
        }
        bufferedReader.close();
    }

    public void writeReturn() throws IOException {
        String fileName = this.getClass().getClassLoader().getResource("").getPath() + "func/return.asm";
        FileInputStream inputStream = new FileInputStream(fileName);
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str = null;

        bw.write("// return\n");
        while ((str = this.bufferedReader.readLine()) != null){
            str = str.trim();
            if(str.startsWith("//") || str.isEmpty()){
                continue;
            }
            bw.write(str + "\n");
        }
        bufferedReader.close();
    }
}
