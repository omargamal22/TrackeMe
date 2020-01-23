package com.example.trackeme;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.example.trackeme.Model.posted_point;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.trackeme.Model.DataBase;

import java.util.List;
import java.util.Observable;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/*public class MyAPP extends Application {

    // uncaught exception handler variable
    private Thread.UncaughtExceptionHandler defaultUEH;

    // handler listener
    private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    Log.e(TAG,
                            "Uncaught Exception thread: "+thread.getName());
                    if(BackgroundService.CheckPointsToPost.size()>0){
                        ThreadB b = new ThreadB();
                        b.start();

                        synchronized(b){
                            try{
                                System.out.println("Waiting for b to complete...");
                                b.wait();
                            }catch(InterruptedException e){
                                e.printStackTrace();
                            }

                            System.out.println("Total is: " + b.total);
                        }
                    }

                }
                    // re-throw critical exception further to the os (important)
                    defaultUEH.uncaughtException(thread, ex);
                }
            };

    public MyAPP() {
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

        // setup handler for uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);
    }
    class ThreadB extends Thread{
        Thread a = new Thread();

        @Override
        public void run(){
            synchronized(this){

                notify();
            }
        }
    }
}*/
public class MyAPP implements Thread.UncaughtExceptionHandler{

    private Context context;
    private  FragmentActivity FA;
    public MyAPP(Context context , FragmentActivity FA){
        this.context=context;
        this.FA = FA;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {

        if(BackgroundService.CheckPointsToPost.size()>0){
            ThreadB B = new ThreadB(BackgroundService.CheckPointsToPost);
            B.start();
            synchronized (B){
                try {
                    B.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        Crashlytics.logException(e);
        System.exit(10);
        Thread.getDefaultUncaughtExceptionHandler().uncaughtException(t,e);
    }
    class ThreadB extends Thread{
        DataBase dataBase = DataBase.getInstance(context);
        List<posted_point> posted_points;
        public ThreadB(List<posted_point> p){
            posted_points = p;
        }
        @Override
        public void run(){
            synchronized(this){
                dataBase.Data_Base_quaries().insertPoints(posted_points);
                notify();

            }
        }
    }
}

