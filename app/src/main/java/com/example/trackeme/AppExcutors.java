package com.example.trackeme;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExcutors{
        // For Singleton instantiation
        private static final Object LOCK = new Object();
        private static AppExcutors sInstance;
        private final Executor diskIO;
        private final Executor networkIO;

        private AppExcutors(Executor diskIO, Executor networkIO) {
            this.diskIO = diskIO;
            this.networkIO = networkIO;
        }

        public static AppExcutors getInstance() {
            if (sInstance == null) {
                synchronized (LOCK) {
                    sInstance = new AppExcutors(Executors.newSingleThreadExecutor(),Executors.newFixedThreadPool(2));
                }
            }
            return sInstance;
        }

        public Executor diskIO() {
            return diskIO;
        }

}
