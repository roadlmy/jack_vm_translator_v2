// 1. get return address: addr = *(LCL - 5)
@LCL
D=M
@5
D=D-A
A=D
D=M
// 暂存 addr 到R13
@R13
M=D

// 2. set 栈顶元素到 ARG指针处
@SP
AM=M-1
D=M
@ARG
A=M
M=D

// 3. SP = ARG + 1
@ARG
D=M+1
@SP
M=D

// 4. 复原LCL,ARG,THIS,THAT指针
@LCL
AM=M-1
D=M
@THAT
M=D
@LCL
AM=M-1
D=M
@THIS
M=D
@LCL
AM=M-1
D=M
@ARG
M=D
@LCL
AM=M-1
D=M
@LCL
M=D

// 5. goto retAddr 第一步存了，直接拿
@R13
A=M
0;JMP

