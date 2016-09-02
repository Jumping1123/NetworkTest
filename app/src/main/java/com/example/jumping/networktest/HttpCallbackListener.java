package com.example.jumping.networktest;

/**
 * Created by Jumping on 2016/8/25.
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
