package com.cloud.common.data.interceptor;

import lombok.extern.java.Log;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * OkHttp 使用 拦截器
 * @author Aijm
 * @since 2019/7/31
 */
@Component
@Log
public class OkHttpTokenInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {

        return null;
    }
}
