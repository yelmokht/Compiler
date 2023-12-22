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
	%x = alloca i32
	%y = alloca i32
	%z = alloca i32
	%a = alloca i32
	br label %whileLoop_0
	whileLoop_0:
		%1 = icmp slt i32 0, %x
		%2 = icmp slt i32 %y, %z
		%3= or i32 1, 2
		br i1 %3, label %whileBody_0, label %whileEnd_0
	whileBody_0:
		store i32 %x, i32* %a
		br label %whileLoop_0
	whileEnd_0:
	ret i32 0
}
