@.strR = private unnamed_addr constant [3 x i8] c"%d\00", align 1

define i32 @readInt() {
  %x = alloca i32, align 4
  %1 = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.strR, i32 0, i32 0), i32* %x)
  %2 = load i32, i32* %x, align 4
  ret i32 %2
}
declare i32 @__isoc99_scanf(i8*, ...)@.strP = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1

; Function Attrs: nounwind uwtable
define void @println(i32 %x) #0 {
  %1 = alloca i32, align 4
  store i32 %x, i32* %1, align 4
  %2 = load i32, i32* %1, align 4
  %3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)
  ret void
}

declare i32 @printf(i8*, ...) #1;

define i32 @main() {
	%a = alloca i32
	%b = alloca i32
	%c = alloca i32
	%1 = call i32 @readInt()
	store i32 %1, i32* %a
	%2 = call i32 @readInt()
	store i32 %2, i32* %b
	br label %whileLoop_0
	whileLoop_0:
		%3 = load i32, i32* %b
		%4 = icmp slt i32 0, %3
		br i1 %4, label %whileBody_0, label %whileEnd_0
	whileBody_0:
		%5 = load i32, i32* %b
		store i32 %5, i32* %c
		br label %whileLoop_1
	whileLoop_1:
		%6 = load i32, i32* %b
		%7 = load i32, i32* %a
		%8 = add i32 %7, 1
		%9 = icmp slt i32 %6, %8
		br i1 %9, label %whileBody_1, label %whileEnd_1
	whileBody_1:
		%10 = load i32, i32* %a
		%11 = load i32, i32* %b
		%12 = sub i32 %10, %11
		store i32 %12, i32* %a
		br label %whileLoop_1
	whileEnd_1:
		%13 = load i32, i32* %a
		store i32 %13, i32* %b
		%14 = load i32, i32* %c
		store i32 %14, i32* %a
		br label %whileLoop_0
	whileEnd_0:
		%15 = load i32, i32* %a
		call void @println(i32 %15)
		ret i32 0
	}
