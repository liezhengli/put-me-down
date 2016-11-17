package com.fzuclover.putmedown.features.register;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.fzuclover.putmedown.networks.OkHttpManager;
import com.fzuclover.putmedown.networks.callback.HttpCallBack;
import com.fzuclover.putmedown.networks.data.HttpUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by lkl on 2016/11/4.
 */

public class RegisterPresenter implements RegisterContract.Presenter {

    private RegisterContract.View mView;

    private EventHandler mEventHandler;

    public RegisterPresenter(RegisterContract.View view) {
        this.mView = view;

        //手机短信验证
        SMSSDK.initSDK((Context)mView, "19219815dbf5a", "87fcdb78d2a73bb58b9791b2918d5f16");
        mEventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        register(mView.getPhone(), mView.getPassword());
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        if(result == SMSSDK.RESULT_COMPLETE){
                            boolean smart = (boolean) data;
                            if(smart){
                                Log.d("获取验证码","这个手机号不用验证");
                            }else{
                            }
                        }
                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(mEventHandler);

    }

    @Override
    public void getCode(String phone) {
        SMSSDK.getVerificationCode("86", phone);
    }

    @Override
    public void submit(String phone, String code) {
        SMSSDK.submitVerificationCode("86", phone, code);
    }

    @Override
    public void unRegisterHandler() {
        SMSSDK.initSDK((Context)mView, "19219815dbf5a", "87fcdb78d2a73bb58b9791b2918d5f16");
        SMSSDK.unregisterEventHandler(mEventHandler);
    }

    private void register(String phone, String password){
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", phone);
        params.put("password", password);
        okHttpManager.okPost(HttpUrl.REGISTER_URL, params, new HttpCallBack() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("success")){
                        mView.toLoginActivity();
                        Toast.makeText((Context)mView, "注册成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText((Context)mView, "账号已被使用", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText((Context)mView, "请输入正确信息", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String err) {
                Toast.makeText((Context)mView, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
