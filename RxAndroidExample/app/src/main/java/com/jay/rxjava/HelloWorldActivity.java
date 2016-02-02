package com.jay.rxjava;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class HelloWorldActivity extends Activity implements View.OnClickListener {
  TextView mMessage;
  Button mClickMe;

  public static Observable<String> mObservable;
  Subscriber<String> mSubscriber;
  Subscription mSubscription;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_hello_world);

    mMessage = (TextView) findViewById(R.id.message);
    mClickMe = (Button) findViewById(R.id.click_me);
    mClickMe.setEnabled(true);
    mClickMe.setOnClickListener(this);

    mObservable = Observable.create(
        new Observable.OnSubscribe<String>() {
          @Override
          public void call(Subscriber<? super String> sub) {
            try {
              Thread.sleep(1000);
              sub.onNext("Hello");
              Thread.sleep(1000);
              sub.onNext(".");
              Thread.sleep(1000);
              sub.onNext("World");
              Thread.sleep(1000);
              sub.onNext("!!!");
              sub.onCompleted();
            } catch (Exception e) {
              sub.onError(e);
            }
          }
        }
    );

    mObservable = mObservable.subscribeOn(Schedulers.newThread());
    mObservable = mObservable.observeOn(AndroidSchedulers.mainThread());

    mSubscriber = new Subscriber<String>() {
      @Override
      public void onCompleted() {
        mMessage.append("\nDone!");
        mClickMe.setEnabled(true);
      }

      @Override
      public void onError(Throwable e) {
        mMessage.append("ERROR!\n" + e.getMessage());
      }

      @Override
      public void onNext(String s) {
        mMessage.append(s);
      }
    };
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mSubscription = mObservable.subscribe(mSubscriber);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mSubscription.unsubscribe();
  }

  @Override
  public void onClick(View v) {
    mMessage.setText(null);
    mObservable = Observable.create(sub -> {
      try {
        Thread.sleep(1000);
        sub.onNext("Hello");
        Thread.sleep(1000);
        sub.onNext(".Again.");
        Thread.sleep(1000);
        sub.onNext("World");
        Thread.sleep(1000);
        sub.onNext("!!!");
        sub.onCompleted();
      } catch (Exception e) {
        sub.onError(e);
      }
    });

    mSubscription = mObservable.subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(mMessage::append,
            error -> mMessage.append("ERROR " + error.getMessage()),
            () -> mMessage.append("\nDone!"));
  }
}
