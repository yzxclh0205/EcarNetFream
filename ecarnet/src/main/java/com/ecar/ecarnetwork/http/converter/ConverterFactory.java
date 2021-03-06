package com.ecar.ecarnetwork.http.converter;

import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 *自定义的转换器工厂类
 */
public class ConverterFactory extends Converter.Factory {

    private final Gson gson;

    public static ConverterFactory create() {
        return create(new Gson());
    }

    public static ConverterFactory create(Gson gson) {
        return new ConverterFactory(gson);
    }

    private ConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException(ConverterFactory.class.getSimpleName()+":gson == null");
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new ResponseConverter<>(gson, type);
    }

//    @Override
//    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
//        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
//        return new RequestConverter<>(gson, adapter);
//    }
}
