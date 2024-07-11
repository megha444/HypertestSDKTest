package com.example.hypertest.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

public class RecordingAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        String mode = System.getenv("HT_MODE");
        if ("REPLAY".equalsIgnoreCase(mode)) {
            System.out.println("Inside replay mode");
            new AgentBuilder.Default()
                .type(ElementMatchers.named("org.springframework.web.client.RestTemplate"))
                .transform(new AgentBuilder.Transformer() {
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, 
                                                            TypeDescription typeDescription, 
                                                            ClassLoader classLoader, 
                                                            JavaModule javaModule, 
                                                            ProtectionDomain protectionDomain) {
                        return builder.method(ElementMatchers.named("execute"))
                                .intercept(MethodDelegation.to(ReplayInterceptor.class).andThen(SuperMethodCall.INSTANCE));
                    }
                })
                .installOn(inst);

            new AgentBuilder.Default()
                .type(ElementMatchers.named("org.springframework.jdbc.core.JdbcTemplate"))
                .transform(new AgentBuilder.Transformer() {
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, 
                                                            TypeDescription typeDescription, 
                                                            ClassLoader classLoader, 
                                                            JavaModule javaModule, 
                                                            ProtectionDomain protectionDomain) {
                        return builder.method(ElementMatchers.named("update"))
                                .intercept(MethodDelegation.to(ReplayInterceptor.class));
                    }
                })
                .installOn(inst);
            
        } else {
            System.out.println("Inside record mode");
            new AgentBuilder.Default()
                .type(ElementMatchers.named("org.springframework.web.client.RestTemplate"))
                .transform(new AgentBuilder.Transformer() {
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, 
                                                            TypeDescription typeDescription, 
                                                            ClassLoader classLoader, 
                                                            JavaModule javaModule, 
                                                            ProtectionDomain protectionDomain) {
                        return builder.method(ElementMatchers.named("execute"))
                                .intercept(MethodDelegation.to(RecordInterceptor.class));
                    }
                })
                .installOn(inst);

            new AgentBuilder.Default()
                .type(ElementMatchers.named("org.springframework.jdbc.core.JdbcTemplate"))
                .transform(new AgentBuilder.Transformer() {
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, 
                                                            TypeDescription typeDescription, 
                                                            ClassLoader classLoader, 
                                                            JavaModule javaModule, 
                                                            ProtectionDomain protectionDomain) {
                        return builder.method(ElementMatchers.named("update"))
                                .intercept(MethodDelegation.to(RecordInterceptor.class));
                    }
                })
                .installOn(inst);
        }
    }

    public static class ReplayInterceptor {
        
        
        public static void intercept(@Origin Method m, Object returnValue) {
            if (m.getName().contains("execute")) {
                returnValue = "Hardcoded HTTP Response";
            } else if (m.getName().contains("update")) {
                returnValue = 1;
            }
            System.out.println("Method exit: " + m.getName() + ", returning hardcoded value: " + returnValue);
        }
    }

    public static class RecordInterceptor {
        
        public static void intercept(@Origin Method m, Object returnValue) {
            System.out.println("Method exit: " + m.getName() + ", return value: " + returnValue);
        } 
    }
}
