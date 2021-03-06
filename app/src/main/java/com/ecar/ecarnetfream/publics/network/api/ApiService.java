package com.ecar.ecarnetfream.publics.network.api;




import com.ecar.ecarnetfream.login.entity.ResLogin;
import com.ecar.ecarnetwork.bean.ResBase;

import java.util.TreeMap;

import okhttp3.MultipartBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;


/**
 * ===============================================
 * <p>
 * 类描述: retrofit 网络请求示例类
 * <p>
 * 创建人:   happy
 * <p>
 * 创建时间: 2016/6/22 0022 上午 10:29
 * <p>
 * 修改人:   happy
 * <p>
 * 修改时间: 2016/6/22 0022 上午 10:29
 * <p>
 * 修改备注:
 * <p>
 * ===============================================
 */
public interface ApiService {
    /**
     * 获取登录的信息
     */
    @GET(".")
    Observable<ResLogin> login(@QueryMap TreeMap<String, String> map);

    @Multipart
    @POST("")
    Observable<ResBase> uploadPic(@Url String url, @QueryMap TreeMap<String, String> map, @Part MultipartBody.Part file);

}



