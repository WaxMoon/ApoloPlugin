package org.apolo;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

class MethodEntity {
    boolean _static;
    boolean _constructor;
    Class<?>[] paramTypes;
    String name;
    Class<?> declaredClass;
    Member member;
    Method proxyMethod;

    public MethodEntity(Class<?> target, Method method) {
        declaredClass = target;
        proxyMethod = method;
    }
}
