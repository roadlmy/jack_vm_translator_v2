// SP = 256
@256
D=A
@SP
M=D

// call Sys.init 0
// 1. boot Code就会调用入口的Sys的init函数，所以Sys的init函数顺序不重要，放哪里都可以
// 2. vm文件是完全可以分开单独编译的！！ 使用（LABEL）去定位位置！

// push retAddrAddress **如何实现:首先汇编代码会把@retAddrLabel替换成具体的行号，那么(retAddrLabel)处就是函数代码执行返回完继续执行的位置
@rretAddrLabel
D=A
@SP
A=M
M=D
@SP
M=M+1

// push LCL
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1

// push ARG
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1

// push THIS
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1

// push THAT
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1

// ARG = SP - 5 - nARGS(0) = SP - 5
@5
D=A
@SP
D=M-D
@ARG
M=D

// LCL = SP
@SP
D=M
@LCL
M=D

// goto Sys.init **如何找到这个函数的行号:使用标签
@functionName
0;JMP

// 设置call完成后继续执行的标签(retAddrLabel)
(rretAddrLabel)


