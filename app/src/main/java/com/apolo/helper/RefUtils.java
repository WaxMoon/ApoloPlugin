package com.apolo.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RefUtils {
    public static Field getField(Class refClass, boolean isStatic, String filedName) {
        if (refClass == null || filedName == null) {
            return null;
        }
        try {
            return refClass.getDeclaredField(filedName);
        } catch (NoSuchFieldException e) {

        }
        return null;
    }

    public static Method getMethod(Class refClass, boolean isStatic, String funcName, Class[] paramTypes) {
        if (refClass == null || funcName == null) {
            return null;
        }
        try {
            return refClass.getDeclaredMethod(funcName, paramTypes);
        } catch (NoSuchMethodException e) {

        }
        return null;
    }

    public static class FieldRef<T> {
        boolean mIsStatic;
        Field mField;
        public FieldRef(Class refClass, boolean isStatic, String name) {
            mIsStatic = isStatic;
            mField = getField(refClass, isStatic, name);
            if (mField != null) {
                mField.setAccessible(true);
            }
        }
        public FieldRef(String className, boolean isStatic, String name) {
            try {
                Class targetClass = Class.forName(className);
                mField = getField(targetClass, isStatic, name);
            } catch (ClassNotFoundException e) {
            }
            mIsStatic = isStatic;
            if (mField != null) {
                mField.setAccessible(true);
            }
        }

        public boolean isValid() {
            return mField != null;
        }

        public T get(Object instance) {
            try {
                return (T) mField.get(instance);
            } catch (Exception e) {

            }
            return null;
        }
        public void set(Object instance, T value) {
            try {
                mField.set(instance, value);
            } catch (Exception e) {

            }
        }
    }

    public static class MethodRef<T> {
        Method mMethod;
        public MethodRef(String className, boolean isStatic, String funcName, Class[] paramsTypes) {
            try {
                Class targetClass = Class.forName(className);
                mMethod = getMethod(targetClass, isStatic, funcName, paramsTypes);
            } catch (Exception e) {
            }
            if (mMethod != null) {
                mMethod.setAccessible(true);
            }
        }
        public MethodRef(Class refClass, boolean isStatic, String funcName, Class[] paramsTypes) {
            mMethod = getMethod(refClass, isStatic, funcName, paramsTypes);
            if (mMethod != null) {
                mMethod.setAccessible(true);
            }
        }

        public boolean isValid() {
            return mMethod != null;
        }

        public T invoke(Object instance, Object[] args) {
            try {
                return (T) mMethod.invoke(instance, args);
            } catch (Exception e) {
            }
            return null;
        }
    }
}
