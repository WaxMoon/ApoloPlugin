package com.apolo.helper;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLogUtils {
    private static final String TAG = FileLogUtils.class.getSimpleName();
    private static FileLogUtils instance;

    private Context mAppContext;
    private File mSaveDir;

    private final String mLogFile = "log.txt";

    private FileLogUtils() {

    }

    public static FileLogUtils getInstance() {
        if (instance == null) {
            synchronized (FileLogUtils.class) {
                if (instance == null) {
                    instance = new FileLogUtils();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        mAppContext = context;
        mSaveDir = mAppContext.getExternalCacheDir();
    }

    private void initSaveDirIfNeed() {
        if (mSaveDir == null) {
            synchronized (this) {
                if (mSaveDir == null) {
                    mSaveDir = ProcessUtils.getMainApplication().getExternalCacheDir();
                }
            }
        }
    }


    /**
     * 清空Log
     */
    public void clearLogs() {
        initSaveDirIfNeed();
        File file_log = new File(mSaveDir, mLogFile);
        if (!file_log.exists()) {
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file_log);
            fos.write("".getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 追加保存Log
     */
    public void saveLog(String content) {
        initSaveDirIfNeed();
        if (!mSaveDir.exists()) {
            Log.e(TAG, "saveLogs mkdir");
            mSaveDir.mkdir();
        }

        Log.e(TAG, "SaveDir=" + mSaveDir);

        RandomAccessFile randomAccessFile = null;

        try {

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.setLength(0);
            //拼接一个日期
            stringBuffer.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
            stringBuffer.append(" ");
            stringBuffer.append(content);
            stringBuffer.append("\n");

            File file_log = new File(mSaveDir, mLogFile);
            if (!file_log.exists()) {
                file_log.createNewFile();
            }

            randomAccessFile = new RandomAccessFile(file_log, "rw");
            randomAccessFile.seek(file_log.length());
            randomAccessFile.write(stringBuffer.toString().getBytes("UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
