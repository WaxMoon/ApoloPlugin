package hook.javax.net.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import org.apolo.ArtEngine;
import org.apolo.HookName;
import org.apolo.ThisObject;
import hook.utils.Slog;

@HookName("javax.net.ssl.HttpsURLConnection")
public class HttpsURLConnection {
    private static final String TAG = HttpsURLConnection.class.getSimpleName();

    @HookName("getDefaultSSLSocketFactory")
    public static SSLSocketFactory getDefaultSSLSocketFactory() {
        Slog.d(TAG, "proxy_getDefaultSSLSocketFactory called", new Exception());
        return ArtEngine.callOrigin(null);
    }

    @HookName("setSSLSocketFactory")
    public static void setSSLSocketFactory(@ThisObject Object thiz, SSLSocketFactory sf) {
        Slog.d(TAG, "proxy_setSSLSocketFactory called", new Exception());
        ArtEngine.callOrigin(thiz, sf);
    }

    @HookName("setHostnameVerifier")
    public static void setHostnameVerifier(@ThisObject Object thiz, HostnameVerifier v) {
        Slog.d(TAG, "proxy_setHostnameVerifier called", new Exception());
        ArtEngine.callOrigin(thiz, v);
    }
}
