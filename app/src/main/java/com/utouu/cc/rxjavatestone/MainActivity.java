package com.utouu.cc.rxjavatestone;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 任新 on 2017/1/16 11:08.
 * Function: RxJava测试主界面
 * Desc: 主要验证 RxJava 1.x 与 RxJava 2.x 的different
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_ONE = "MainActivity-RxJava 1.x";
    private static final String TAG_TWO = "MainActivity-RxJava 2.x";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RxJava 1.x 简单使用
//        toSimpleUseRxJavaOne();
        //RxJava 2.x 简单使用
        toSimpleUseRxJavaTwo();
        //RxJava 2.x 简洁编写
        Flowable.just("hello RxJava 2 简洁")
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG_TWO, s);
                    }
                });
        //map操作符：把一个事件转换成另一个事件
        Flowable.just("这是第一个事件")
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return s + " -map转换了：这是第二个事件";
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG_TWO, s);
                    }
                });
        //map操作符高级用法：得到字符串的hashcode
        Flowable.just("map高级")
                .map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String s) throws Exception {
                        return s.hashCode();
                    }
                })
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return integer.toString();
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG_TWO, s);
                    }
                });
        //flatMap的用法
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(3);
        list.add(5);
        Flowable.just(list)
                .flatMap(new Function<List<Integer>, Publisher<Integer>>() {
                    @Override
                    public Publisher<Integer> apply(List<Integer> integers) throws Exception {
                        return Flowable.fromIterable(integers);
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG_TWO, String.valueOf(integer));
                    }
                });

        //filter的使用：过滤
        Flowable.fromArray(1, 23, 45, -22, 44)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer.intValue() > 22;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG_TWO, String.valueOf(integer));
                    }
                });

        //take的使用：用于指定订阅者最多收到多少数据。
        Flowable.fromArray(1, 23, 45, -22, 44)
                .take(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG_TWO, String.valueOf(integer));
                    }
                });

        //doOnNext:允许我们在每次输出一个元素之前做一些额外的事情。
        Flowable.just(1, 3, 4)
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG_TWO, "我是第" + integer + "名");
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG_TWO, String.valueOf(integer));
                    }
                });

        /**
         * Flowable总共发射了两个数据，但中间延时了3秒，如果在主线程中延时，那将会导致UI卡顿，这是绝对不能容忍的。
         * 所以在订阅之前，我们使用 subscribeOn(Schedulers.io()) 指定了发送数据是在io线程(某个子线程)，
         * 然后调用 observeOn(AndroidSchedulers.mainThread()) 指定订阅者在主线程执行。
         */
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                e.onNext("将会在3秒后显示");
                SystemClock.sleep(3000);
                e.onNext("我是神");
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * RxJava 1.x 简单使用
     */
   /* private void toSimpleUseRxJavaOne() {
        //1，创建一个观察者
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.i(TAG_ONE, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG_ONE, "onError");
            }

            @Override
            public void onNext(String s) {
                Log.i(TAG_ONE, s);
            }
        };

        //2，使用Observable.create()创建一个被观察者
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello");
                subscriber.onNext("RxJava 1");
                subscriber.onCompleted();
            }
        });

        //3，订阅
        observable.subscribe(observer);
    }*/

    /**
     * RxJava 2.x 简单使用
     */
    private void toSimpleUseRxJavaTwo() {
        //2，create a flowable
        Flowable<String> flowable = Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                e.onNext("hello RxJava 2");
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER);

        //2，create Subscriber
        org.reactivestreams.Subscriber subscriber = new org.reactivestreams.Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.i(TAG_TWO, "onSubscribe");
                /**
                 * 调用request去请求资源，参数就是要请求的数量，一般如果不限制请求数量，可以写成Long.MAX_VALUE
                 * 注意：如果不调用request，Subscriber的onNext和onComplete方法将不会被调用。
                 */
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(String s) {
                //onNext方法里面传入的参数就是Flowable中发射出来的。
                Log.i(TAG_TWO, s);
            }

            @Override
            public void onError(Throwable t) {
                Log.i(TAG_TWO, "onError");
            }

            @Override
            public void onComplete() {
                Log.i(TAG_TWO, "onComplete");
            }
        };

        //3，订阅
        flowable.subscribe(subscriber);
    }
}
