package hook;

import java.io.IOException;
import org.apolo.ArtEngine;
import org.apolo.HookClass;
import org.apolo.HookName;
import org.apolo.ThisObject;
import hook.utils.Slog;

@HookClass(Test.class)
public class Test {
    @HookName("test")
    public static void test(@ThisObject Object o) {
        Slog.d("test", "test: " + o);
        ArtEngine.callOrigin(o);
    }

    public void test() throws IOException {
        throw new IOException("test");
    }
}
