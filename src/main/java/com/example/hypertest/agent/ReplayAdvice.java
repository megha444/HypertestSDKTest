package com.example.hypertest.agent;

import net.bytebuddy.asm.Advice;

public class ReplayAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin String method) {
        System.out.println("Replaying method: " + method);
    }

    @Advice.OnMethodExit
    public static void onExit(@Advice.Origin String method, @Advice.Return(readOnly = false) Object returnValue) {
        // Provide hardcoded return values for replay mode
        if (method.contains("execute")) {
            returnValue = "Hardcoded HTTP Response"; // This should be the expected return type, e.g., ResponseEntity<String>
        } else if (method.contains("update")) {
            returnValue = 1; // Assume successful DB update
        }
        System.out.println("Method exit: " + method + ", returning hardcoded value: " + returnValue);
    }
}