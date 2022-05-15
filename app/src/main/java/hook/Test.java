package hook;

import android.util.Log;

import java.io.IOException;


import core.apolo.ApoloHook;
import core.apolo.HookClass;
import core.apolo.HookName;
import core.apolo.ThisObject;

@HookClass(Test.class)
public class Test {
    @HookName("test")
    public static void test(@ThisObject Object o) {
        Log.e("test", "test: " + o);
        ApoloHook.callOrigin(o);
    }

    public void test() throws IOException {
        throw new IOException("test");
    }
}
