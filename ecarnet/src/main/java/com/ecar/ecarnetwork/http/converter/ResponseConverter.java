package com.ecar.ecarnetwork.http.converter;

import android.text.TextUtils;
import android.util.Log;

import com.ecar.ecarnetwork.bean.ResBase;
import com.ecar.ecarnetwork.db.SettingPreferences;
import com.ecar.ecarnetwork.http.api.ApiBox;
import com.ecar.ecarnetwork.http.exception.InvalidException;
import com.ecar.ecarnetwork.http.util.InvalidUtil;
import com.ecar.ecarnetwork.http.util.TagLibUtil;
import com.ecar.ecarnetwork.util.uvtKeys;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 自定义响应体的转换器
 */
public class ResponseConverter<T> implements Converter<ResponseBody, T> {

    /**
     * 1.是否分出 ResponseBody value =null 时
     * 选择1.1 抛出自定义NetException。异常判断 ，判断instanceof NetException
     * 选择1.2 判断Http 异常。根据retrofit自动抛出的（疑问，是解析抛出的？还是异常直接抛出？）
     * <p>
     * 2.1 策略1：直接返回json解析。订阅者subscriber 中判断state 自处理
     * <p>
     * 2.2 策略2：根据泛型解析json后 (已采用)
     * 1.1 判断state 成功/失败 （失败抛ResultException，订阅者在OnError中自处理）
     * 成功-->返回json解析（直接返回反序列话后bean 或者二次解析，看需求）
     * 失败-->抛失败异常（1.state--目测一体化没什么用，2.msg）
     * <p>
     * --------->2*2 四种情况
     */
    public static final String TAG = "ResponseConverter";

    private final Gson gson;
    private final Type type;

    ResponseConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(final ResponseBody value) throws IOException {
        ResBase base = null;
        String response = null;
        try {
            response = value.string();
        } catch (IOException e) {
            e.printStackTrace();
            TagLibUtil.showLogDebug("请求成功，获取返回值失败");
            return null;
        }
        Log.i("thread",Thread.currentThread().getName()) ;
        try {
            base = gson.fromJson(response, type);
            if (base != null&& !InvalidUtil.checkSign(base.sign, response)) {//校验错误
                throw new InvalidException(InvalidException.FLAG_ERROR_RESPONCE_CHECK,base.msg,base);
            }

            /**
             * 保存utv
             */
            saveUTV(base);
            if (base != null && base.state != 1) {//非成功
                if (base != null && base.state == 0 && !TextUtils.isEmpty(base.msg) &&
                        (!TextUtils.isEmpty(base.msg) && base.msg.contains("0x04") || !TextUtils.isEmpty(base.msg) && base.msg.contains("0x02"))) {
                    TagLibUtil.showLogDebug("系统级错误 message出现");
                    //重新登录
                    throw new InvalidException(InvalidException.FLAG_ERROR_RELOGIN,base.msg,base);
                } else {//失败 -- 订阅者 自己在onNext做处理逻辑
//                    if(base==null&&!TextUtils.isEmpty(response)){
//                        base.jsonStr=response;
//                    }
//                    throw new UserException(base.code, base.msg,base);
                }
            }
        } finally {
            value.close();
        }
//        if(base!=null&&!TextUtils.isEmpty(response)){
//            base.jsonStr=response;
//        }
        return (T) base;
    }

    /**
     * 保存 u 、v 、t 到sp
     *
     * @param resBase
     */
    private void saveUTV(ResBase resBase) {
        if (resBase == null) {
            return;
        }
        try {
            SettingPreferences sp = SettingPreferences.getDefault(ApiBox.getInstance().application);
            if (!TextUtils.isEmpty(resBase.v)) {
                sp.setV(resBase.v);
                TagLibUtil.showLogDebug("v=" + resBase.v);
                uvtKeys.vKey = resBase.v;
            }
            if (!TextUtils.isEmpty(resBase.t)) {
                sp.setT(resBase.t);
                TagLibUtil.showLogDebug("t=" + resBase.t);
                uvtKeys.tKey = resBase.t;

            }
            if (!TextUtils.isEmpty(resBase.u)) {
                sp.setU(resBase.u);
                TagLibUtil.showLogDebug("u=" + resBase.u);
                uvtKeys.uKey = resBase.u;

            }
            if (!TextUtils.isEmpty(String.valueOf(resBase.ts))) {
                sp.setTs(String.valueOf(resBase.ts));
                uvtKeys.ts = resBase.ts;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
