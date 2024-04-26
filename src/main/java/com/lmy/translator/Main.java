package com.lmy.translator;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {

        // 1. 程序入口：java -jar jack.jar [filename]/[directory]
        if (args.length != 1) {
            throw new RuntimeException("You must Enter file or directory and not more than 1 argument");
        }
        String input = args[0];
        System.out.println("参数" + 1 + "的值为：" + input);
        String output;

        // 2. 文件处理：如果单一文件直接加入列表，如果多文件，将多文件逐个加入列表
        List<File> handleFile = new ArrayList<>();
        if(input.contains(".vm")){
            File fileIn = new File(input);
            handleFile.add(fileIn);
            output = fileIn.getAbsolutePath().substring(0, fileIn.getAbsolutePath().lastIndexOf(".")) + ".asm";
        }else {
            File fileIn = new File(input);
            String[] filenames = fileIn.list(new MyExtFilter(".vm"));
            for(String name:filenames){
                handleFile.add(new File(input+ "/" + name));
            }
            output = fileIn.getAbsolutePath() + "/" +  fileIn.getName() + ".asm";
        }

        File fileOut = new File(output);
        System.out.println(handleFile);
        System.out.println(output);

        // 3. 编译过程

        // 一次编译只会boot一次code，所以所有文件只会new一个writer
        Writer writer = new Writer(fileOut);
        writer.writeInit();
        // 但一个文件会对应一个parser
        for(File f: handleFile){

            Parser parser = new Parser(f);

            while (parser.hasMoreCommand()){
                String thisLineVmCode = parser.advance();
                // 空行或者注释直接忽略
                if(thisLineVmCode.startsWith("//") || thisLineVmCode.isEmpty()){
                    continue;
                }
                System.out.println(thisLineVmCode);

                // 写入
                if(Objects.equals(parser.commandType(), "C_ARITHMETIC")){
                    writer.writeArithmetic(thisLineVmCode);
                }else if(Objects.equals(parser.commandType(),"C_PUSH") || Objects.equals(parser.commandType(),"C_POP")){
                    writer.writePushPop(parser.commandType(), thisLineVmCode,parser.arg0(), parser.arg1(),f);
                }else if(Objects.equals(parser.commandType(),"C_GOTO") ){
                    writer.writeGoto(parser.arg0());
                }else if(Objects.equals(parser.commandType(),"C_IF") ){
                    writer.writeIf(parser.arg0());
                }else if(Objects.equals(parser.commandType(),"C_LABEL") ){
                    writer.writeLabel(parser.arg0());
                }else if(Objects.equals(parser.commandType(),"C_FUNCTION") ){
                    writer.writeFunction(parser.arg0(),parser.arg1());
                }else if(Objects.equals(parser.commandType(),"C_CALL") ){
                    writer.writeCall(parser.arg0(),parser.arg1());
                }else if(Objects.equals(parser.commandType(),"C_RETURN") ){
                    writer.writeReturn();
                }
            }

            // 关闭parser的连接
            parser.close();
        }
        // 关闭writer的连接
        writer.close();
    }

    static class MyExtFilter implements FilenameFilter {

        private String ext;

        MyExtFilter(String ext) {
            this.ext = ext;
        }

        public boolean accept(File dir, String name) {
            return name.endsWith(ext);
        }

    }
}