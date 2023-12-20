@.strP = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1

define void @println(i32 %x) {
  %1 = alloca i32, align 4
  store i32 %x, i32* %1, align 4
  %2 = load i32, i32* %1, align 4
  %3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)
  ret void
}

declare i32 @printf(i8*, ...)

define i32 @main() {
entry:
    %0 = alloca i32
    store i32 1, i32* %0
    %1 = load i32, i32* %0
    %2 = icmp eq i32 %1, %1
    br i1 %2, label %Assign_0, label %Assign_1

Assign_0:
    %x = alloca i32
    store i32 0, i32* %x
    %y = load i32, i32* %x
    call void @println(i32 %y)
    ret i32 0

Assign_1:
    store i32 1, i32* %x
    %z = load i32, i32* %x
    call void @println(i32 %z)
    ret i32 0
}
