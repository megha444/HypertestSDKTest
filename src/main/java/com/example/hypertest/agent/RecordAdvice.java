package com.example.hypertest.agent;

import net.bytebuddy.asm.Advice;

public class RecordAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin String method) {
        System.out.println("Recording method: " + method);
    }

    @Advice.OnMethodExit
    public static void onExit(@Advice.Origin String method, @Advice.Return Object returnValue) {
        System.out.println("Method exit: " + method + ", return value: " + returnValue);
    }
}
