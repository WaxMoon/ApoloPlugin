package core.apolo.xposed;

import android.os.Build;

import com.android.dx.Code;
import com.android.dx.DexMaker;
import com.android.dx.FieldId;
import com.android.dx.Local;
import com.android.dx.MethodId;
import com.android.dx.TypeId;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.Map;

import core.apolo.ApoloHook;
import core.apolo.xposed.util.DexMakerUtil;
import dalvik.system.InMemoryDexClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static core.apolo.xposed.util.DexMakerUtil.MD5;
import static core.apolo.xposed.util.DexMakerUtil.autoBoxIfNecessary;
import static core.apolo.xposed.util.DexMakerUtil.autoUnboxIfNecessary;
import static core.apolo.xposed.util.DexMakerUtil.createResultLocals;
import static core.apolo.xposed.util.DexMakerUtil.getObjTypeIdIfPrimitive;

public class HookerDexMaker {

    public static final String METHOD_NAME_HOOK = "hook";


    private static final String CLASS_DESC_PREFIX = "L";
    private static final String CLASS_NAME_PREFIX = "ApoloHooker";
    private static final String FIELD_NAME_HOOK_INFO = "additionalHookInfo";
    private static final String FIELD_NAME_METHOD = "method";


    private FieldId<?, XposedBridge.AdditionalHookInfo> mHookInfoFieldId;
    private FieldId<?, Member> mMethodFieldId;
    private MethodId<?, ?> mHookMethodId;
    private MethodId<?, ?> mApoloHookBridgeMethodId;

    private TypeId<?> mHookerTypeId;
    private TypeId<?>[] mParameterTypeIds;
    private Class<?>[] mActualParameterTypes;
    private Class<?> mReturnType;
    private TypeId<?> mReturnTypeId;
    private boolean mIsStatic;
    // TODO use this to generate methods
    private boolean mHasThrowable;

    private DexMaker mDexMaker;
    private Member mMember;
    private XposedBridge.AdditionalHookInfo mHookInfo;
    private ClassLoader mAppClassLoader;
    private Class<?> mHookClass;
    private Method mHookMethod;
    private File mDexDir;


    public void start(Member member, XposedBridge.AdditionalHookInfo hookInfo, ClassLoader appClassLoader, File dexDirPath) throws Exception {
        if (member instanceof Method) {
            Method method = (Method) member;
            mIsStatic = Modifier.isStatic(method.getModifiers());
            mReturnType = method.getReturnType();
            if (mReturnType.equals(Void.class) || mReturnType.equals(void.class)
                    || mReturnType.isPrimitive()) {
                mReturnTypeId = TypeId.get(mReturnType);
            } else {
                // all others fallback to plain Object for convenience
                mReturnType = Object.class;
                mReturnTypeId = TypeId.OBJECT;
            }
            mParameterTypeIds = DexMakerUtil.getParameterTypeIds(method.getParameterTypes(), mIsStatic);
            mActualParameterTypes = DexMakerUtil.getParameterTypes(method.getParameterTypes(), mIsStatic);
            mHasThrowable = method.getExceptionTypes().length > 0;
        } else if (member instanceof Constructor) {
            Constructor constructor = (Constructor) member;
            mIsStatic = false;
            mReturnType = void.class;
            mReturnTypeId = TypeId.VOID;
            mParameterTypeIds = DexMakerUtil.getParameterTypeIds(constructor.getParameterTypes(), mIsStatic);
            mActualParameterTypes = DexMakerUtil.getParameterTypes(constructor.getParameterTypes(), mIsStatic);
            mHasThrowable = constructor.getExceptionTypes().length > 0;
        } else if (member.getDeclaringClass().isInterface()) {
            throw new IllegalArgumentException("Cannot hook interfaces: " + member.toString());
        } else if (Modifier.isAbstract(member.getModifiers())) {
            throw new IllegalArgumentException("Cannot hook abstract methods: " + member.toString());
        } else {
            throw new IllegalArgumentException("Only methods and constructors can be hooked: " + member.toString());
        }
        mMember = member;
        mHookInfo = hookInfo;
        mDexDir = dexDirPath;
        mAppClassLoader = appClassLoader;
        mDexMaker = new DexMaker();
        // Generate a Hooker class.
        String className = getClassName(mMember);
        String dexName = className + ".apk";

        Method hookEntity = null;
        //try load cache first
        try {
            ClassLoader loader = mDexMaker.loadClassDirect(mAppClassLoader, mDexDir, dexName);
            if (loader != null) {
                hookEntity = loadHookerClass(loader, className);
            }
        } catch (Throwable throwable) {
        }

        //do generate
        if (hookEntity == null) {
            hookEntity = doMake(className, dexName);
        }
        ApoloHook.addHook(member, hookEntity);
    }

    private Method doMake(String className, String dexName) throws Exception {
        mHookerTypeId = TypeId.get(CLASS_DESC_PREFIX + className + ";");
        mDexMaker.declare(mHookerTypeId, className + ".generated", Modifier.PUBLIC, TypeId.OBJECT);
        generateHookMethod();

        ClassLoader loader = null;
        if (mDexDir == null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                throw new IllegalArgumentException("dexDirPath should not be empty!!!");
            } else {
                byte[] dexBytes = mDexMaker.generate();
                loader = new InMemoryDexClassLoader(ByteBuffer.wrap(dexBytes), mAppClassLoader);
            }
        } else {
            // Create the dex file and load it.
            try {
                loader = mDexMaker.generateAndLoad(mAppClassLoader, mDexDir, dexName);
            } catch (IOException e) {
                //can not write file
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    byte[] dexBytes = mDexMaker.generate();
                    loader = new InMemoryDexClassLoader(ByteBuffer.wrap(dexBytes), mAppClassLoader);
                }
            }
        }
        if (loader == null)
            return null;
        return loadHookerClass(loader, className);
    }

    private Method loadHookerClass(ClassLoader loader, String className) throws Exception {
        mHookClass = loader.loadClass(className);
        // Execute our newly-generated code in-process.
        mHookMethod = mHookClass.getMethod(METHOD_NAME_HOOK, mActualParameterTypes);
        setup(mHookClass);
        return mHookMethod;
    }

    private void setup(Class<?> target) {
        XposedHelpers.setStaticObjectField(target, FIELD_NAME_METHOD, mMember);
        XposedHelpers.setStaticObjectField(target, FIELD_NAME_HOOK_INFO, mHookInfo);
    }

    private String getClassName(Member originMethod) {
        return CLASS_NAME_PREFIX + "_" + MD5(originMethod.toString());
    }


    private void generateHookMethod() {
        mHookInfoFieldId = mHookerTypeId.getField(DexMakerUtil.TYPE_XPOSED_BRIDGE_ADDITIONAL_HOOK_INFO, FIELD_NAME_HOOK_INFO);
        mMethodFieldId = mHookerTypeId.getField(DexMakerUtil.TYPE_MEMBER, FIELD_NAME_METHOD);

        mDexMaker.declare(mHookInfoFieldId, Modifier.STATIC, null);
        mDexMaker.declare(mMethodFieldId, Modifier.STATIC, null);

        mHookMethodId = mHookerTypeId.getMethod(mReturnTypeId, METHOD_NAME_HOOK, mParameterTypeIds);

        mApoloHookBridgeMethodId = TypeId.get(HookerDexMaker.class).getMethod(DexMakerUtil.TYPE_OBJECT, "hookBridge", DexMakerUtil.TYPE_MEMBER, DexMakerUtil.TYPE_XPOSED_BRIDGE_ADDITIONAL_HOOK_INFO, DexMakerUtil.TYPE_OBJECT, DexMakerUtil.TYPE_OBJECT_ARRAY);

        Code code = mDexMaker.declare(mHookMethodId, Modifier.PUBLIC | Modifier.STATIC);

        Local<Member> originMethod = code.newLocal(DexMakerUtil.TYPE_MEMBER);
        Local<XposedBridge.AdditionalHookInfo> hookInfo = code.newLocal(DexMakerUtil.TYPE_XPOSED_BRIDGE_ADDITIONAL_HOOK_INFO);
        Local<Object> thisObject = code.newLocal(TypeId.OBJECT);
        Local<Object[]> args = code.newLocal(DexMakerUtil.TYPE_OBJECT_ARRAY);
        Local<Integer> actualParamSize = code.newLocal(TypeId.INT);
        Local<Integer> argIndex = code.newLocal(TypeId.INT);
        Local<Object> resultObj = code.newLocal(TypeId.OBJECT);

        Local[] allArgsLocals = createParameterLocals(code);
        Map<TypeId, Local> resultLocals = createResultLocals(code);


        code.loadConstant(args, null);
        code.loadConstant(argIndex, 0);
        code.sget(mMethodFieldId, originMethod);
        code.sget(mHookInfoFieldId, hookInfo);

        int paramsSize = mParameterTypeIds.length;
        int offset = 0;
        // thisObject
        if (mIsStatic) {
            // thisObject = null
            code.loadConstant(thisObject, null);
        } else {
            // thisObject = args[0]
            offset = 1;
            code.move(thisObject, allArgsLocals[0]);
        }

        // actual args (exclude thisObject if this is not a static method)
        code.loadConstant(actualParamSize, paramsSize - offset);
        code.newArray(args, actualParamSize);
        for (int i = offset; i < paramsSize; i++) {
            Local parameter = allArgsLocals[i];
            // save parameter to resultObj as Object
            autoBoxIfNecessary(code, resultObj, parameter);
            code.loadConstant(argIndex, i - offset);
            // save Object to args
            code.aput(args, argIndex, resultObj);
        }

        if (mReturnTypeId.equals(TypeId.VOID)) {
            code.invokeStatic(mApoloHookBridgeMethodId, null, originMethod, hookInfo, thisObject, args);
            code.returnVoid();
        } else {
            code.invokeStatic(mApoloHookBridgeMethodId, resultObj, originMethod, hookInfo, thisObject, args);
            TypeId objTypeId = getObjTypeIdIfPrimitive(mReturnTypeId);
            Local matchObjLocal = resultLocals.get(objTypeId);
            code.cast(matchObjLocal, resultObj);
            // have to use matching typed Object(Integer, Double ...) to do unboxing
            Local toReturn = resultLocals.get(mReturnTypeId);
            autoUnboxIfNecessary(code, toReturn, matchObjLocal, resultLocals, true);
            code.returnValue(toReturn);
        }

    }

    private Local[] createParameterLocals(Code code) {
        Local[] paramLocals = new Local[mParameterTypeIds.length];
        for (int i = 0; i < mParameterTypeIds.length; i++) {
            paramLocals[i] = code.getParameter(i, mParameterTypeIds[i]);
        }
        return paramLocals;
    }

    public static Object hookBridge(Member origin, XposedBridge.AdditionalHookInfo additionalHookInfo, Object thiz, Object... args) throws Throwable {
        if (XposedBridge.disableHooks) {
            return ApoloHook.callOrigin(thiz, args);
        }
        Object[] snapshot = additionalHookInfo.callbacks.getSnapshot();

        if (snapshot == null || snapshot.length == 0) {
            return ApoloHook.callOrigin(thiz, args);
        }

        XC_MethodHook.MethodHookParam param = new XC_MethodHook.MethodHookParam();

        param.method = origin;
        param.thisObject = thiz;
        param.args = args;

        int beforeIdx = 0;
        do {
            try {
                ((XC_MethodHook) snapshot[beforeIdx]).callBeforeHookedMethod(param);
            } catch (Throwable t) {
                // reset result (ignoring what the unexpectedly exiting callback did)
                param.setResult(null);
                param.returnEarly = false;
                continue;
            }

            if (param.returnEarly) {
                // skip remaining "before" callbacks and corresponding "after" callbacks
                beforeIdx++;
                break;
            }
        } while (++beforeIdx < snapshot.length);

        // call original method if not requested otherwise
        if (!param.returnEarly) {
            try {
                param.setResult(ApoloHook.callOrigin(thiz, param.args));
            } catch (Throwable e) {
                XposedBridge.log(e);
                param.setThrowable(e);
            }
        }

        // call "after method" callbacks
        int afterIdx = beforeIdx - 1;
        do {
            Object lastResult = param.getResult();
            Throwable lastThrowable = param.getThrowable();

            try {
                ((XC_MethodHook) snapshot[afterIdx]).callAfterHookedMethod(param);
            } catch (Throwable t) {
                XposedBridge.log(t);
                if (lastThrowable == null)
                    param.setResult(lastResult);
                else
                    param.setThrowable(lastThrowable);
            }
        } while (--afterIdx >= 0);
        if (!param.hasThrowable()) {
            return param.getResult();
        } else {
            throw param.getThrowable();
        }
    }
}
