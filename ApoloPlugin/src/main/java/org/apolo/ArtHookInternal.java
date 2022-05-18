package org.apolo;

import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;

class ArtHookInternal {
    final static HashMap<Member, Member> methods = new HashMap<>();

    private static final String TAG = "ArtHookInternal";

    public static void addHooker(ClassLoader loader, Class<?> cls, boolean ignoreException) throws Throwable {
        if (loader == null) {
            loader = ArtHookInternal.class.getClassLoader();
        }
        Class<?> target = getHookTarget(loader, cls);

        Method[] methods = cls.getDeclaredMethods();

        for (Method method : methods) {
            try {
                Annotation[][] annotations = method.getParameterAnnotations();
                MethodEntity entity = new MethodEntity(target, method);

                HookName name = method.getAnnotation(HookName.class);
                if (name != null) {
                    entity.name = name.value();
                    entity._constructor = HookName.CONSTRUCTOR.contains(entity.name);
                } else {
                    HookConstructor constructor = method.getAnnotation(HookConstructor.class);
                    if (constructor != null) {
                        entity.name = "<init>";
                        entity._constructor = true;
                    }
                }
                if (entity.name == null) continue;

                if (annotations.length > 0) {
                    for (int i = 0; i < annotations[0].length; i++) {
                        if (annotations[0][i].annotationType() == ThisObject.class) {
                            entity._static = true;
                            break;
                        }

                    }
                }
                int paramIndex = 0;
                if (entity._static) {
                    paramIndex++;
                }
                Class<?>[] types = method.getParameterTypes();
                entity.paramTypes = new Class<?>[types.length - paramIndex];
                for (int i = paramIndex; i < annotations.length; i++) {
                    int index = i - paramIndex;
                    for (int j = 0; j < annotations[i].length; j++) {
                        if (annotations[i][j].annotationType() == HookName.class) {
                            HookName hookName = (HookName) annotations[i][j];
                            entity.paramTypes[index] = loadClass(loader, hookName.value());
                        }
                    }
                    if (entity.paramTypes[index] == null) {
                        entity.paramTypes[index] = types[i];
                    }
                }

                if (entity._constructor) {
                    Constructor<?> constructor = entity.declaredClass.getDeclaredConstructor(entity.paramTypes);
                    constructor.setAccessible(true);
                    entity.member = constructor;
                } else {
                    Method m = entity.declaredClass.getDeclaredMethod(entity.name, entity.paramTypes);
                    m.setAccessible(true);
                    entity.member = m;
                }
                Log.d(TAG, String.format("addHooker: %s.%s", entity.declaredClass.getName(), entity.name));
                ArtHookInternal.methods.put(entity.member, method);
            } catch (Throwable e) {
                if (!ignoreException) {
                    throw e;
                }
                Log.w(TAG, "addHooker: failed " + e);
            }


        }
    }

    private static Class<?> getHookTarget(ClassLoader loader, Class<?> cls) throws Throwable {
        HookClass targetAno = cls.getAnnotation(HookClass.class);
        Class<?> target = null;

        if (targetAno != null) {
            target = targetAno.value();
        } else {
            HookName name = cls.getAnnotation(HookName.class);
            if (name != null) {
                target = loadClass(loader, name.value());
            }
        }
        return target;
    }

    private static Class<?> loadClass(ClassLoader loader, String name) throws Throwable {
        return loader.loadClass(name);
    }

    public static void addHook(Member member, Method proxyMethod) {
        methods.put(member, proxyMethod);
    }

    public static boolean contains(Member member) {
        return methods.containsKey(member);
    }
}
