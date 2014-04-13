package sg.edu.nus.cs2103t.mina.utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import org.apache.logging.log4j.Level;

public class FileLockHelper {

    private static final String FILE_MODE = "rw";

    private static final String LOCK_FILE_EXTENSION = ".lock";

    private static final String USER_HOME = "user.home";

    private static final String CLASS_NAME = FileLockHelper.class.getName();

    private static FileLockHelper instance;

    private String lockFileName;
    private File lockFile;
    private FileChannel channel;
    private FileLock lock;

    private FileLockHelper(String appName) {
        this.lockFileName = appName;
    }

    public static FileLockHelper getFileLockHelperInstance() {
        if (instance == null) {
            instance = new FileLockHelper(
                    ConfigHelper.getProperty(ConfigHelper.LOCK_KEY));
            return instance;
        }
        return instance;
    }

    public synchronized boolean isAppActive() {
        try {
            lockFile = new File(System.getProperty(USER_HOME),
                    lockFileName + LOCK_FILE_EXTENSION);
            channel = new RandomAccessFile(lockFile, FILE_MODE).getChannel();
            try {
                lock = channel.tryLock();
            } catch (OverlappingFileLockException e) {
                // already locked
                closeLock();
                return true;
            }

            if (lock == null) {
                closeLock();
                return true;
            }

            Runtime.getRuntime().addShutdownHook(new Thread() {
                // destroy the lock when the JVM is closing
                public void run() {
                    closeLock();
                    deleteFile();
                }
            });
            return false;
        } catch (Exception e) {
            closeLock();
            return true;
        }
    }

    private void closeLock() {
        try {
            lock.release();
            channel.close();
        } catch (Exception e) {
            LogHelper.log(CLASS_NAME, Level.ERROR, "close lock failed: " + e
                    .getStackTrace().toString());
        }
    }

    private void deleteFile() {
        try {
            lockFile.delete();
        } catch (Exception e) {
            LogHelper.log(CLASS_NAME, Level.ERROR,
                    "delete lock file failed: " + e.getStackTrace().toString());
        }
    }
}
