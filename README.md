# RxJavaTestOne
## RxJava 1.x 的简单使用
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
   ## RxJava 2.x 的简单使用
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
