package com.sunflower.rxandroiddemo.utils;

import com.squareup.okhttp.OkHttpClient;
import com.sunflower.rxandroiddemo.dto.JsonResponse;
import com.sunflower.rxandroiddemo.api.APIService;
import com.sunflower.rxandroiddemo.dto.Response;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sunflower on 2015/11/4.
 */
public class RetrofitUtil {

    private static final String API_HOST = "http://203.195.168.151:8888/sunray/";

    private static APIService service;
    private static Retrofit retrofit;
    private static OkHttpClient client;

    public static APIService getService() {
        if (service == null) {
            service = getRetrofit().create(APIService.class);
        }
        return service;
    }

    static {
        client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.SECONDS);

//        // print Log
//        client.interceptors().add(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//            @Override
//            public void log(String message) {
//                if (message.startsWith("{")) {
//                    Logger.json(message);
//                } else {
//                    Logger.i("Api", message);
//                }
//            }
//        }));
    }

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(API_HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static <T> Observable<T> flatResponse(final Response<T> response) {
        return Observable.create(new Observable.OnSubscribe<T>() {

            @Override
            public void call(Subscriber<? super T> subscriber) {
                switch (response.code) {
                    case Constant.OK:
                        subscriber.onNext(response.object);
                        break;
                    default:
                        subscriber.onError(new APIException(response.code, response.message));
                        break;
                }
                subscriber.onCompleted();
            }
        });
    }


    public static class APIException extends Exception {
        public String code;
        public String message;

        public APIException(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }


}
