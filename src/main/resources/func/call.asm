// 1. 将返回的地址值存入栈，不存的话如果没有arg，那么在return的时候，会直接被栈顶的元素复制过来覆盖
@retAddrLabel
D=A
@SP
A=M
M=D
@SP
M=M+1

// 2. push LCL,ARG,THIS,THAT
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1

// 3. set ARG = SP - 5 - nARGs , LCL = SP
@nArgs
D=A
@5
D=D+A
@SP
D=M-D
@ARG
M=D
@SP
D=M
@LCL
M=D

// 4. goto functionName
@functionName
0;JMP

// 5. add ret label
(retAddrLabel)


