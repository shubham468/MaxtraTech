package com.tech.umr.Network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

 class RetroInstance {

    private var retrofit: Retrofit? = null

    private val baseUrl = "http://182.76.237.238/~apitest/sme_link/api/"

    fun getRetrofit(token: String): Retrofit? {
//        final String token1="eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjcxMWM2YjIyNmQ2NTIyOGExZjgzYzViYTRiNzJjMWJhOTY5ZTc0MjkyYWMxNGY0ZjVhZTg1NmYzOTRiMDE0YWNjNDI1NGVkZTQ1MmI4ZjAyIn0.eyJhdWQiOiIxIiwianRpIjoiNzExYzZiMjI2ZDY1MjI4YTFmODNjNWJhNGI3MmMxYmE5NjllNzQyOTJhYzE0ZjRmNWFlODU2ZjM5NGIwMTRhY2M0MjU0ZWRlNDUyYjhmMDIiLCJpYXQiOjE2MjA2MjExMzUsIm5iZiI6MTYyMDYyMTEzNSwiZXhwIjoxNjUyMTU3MTM1LCJzdWIiOiIxNyIsInNjb3BlcyI6W119.Wu2y8CiG0Uvfo1c79m-zCtbdJmx8Lmnl4b-6jCaRji-eiQUo32D5rREpdjaZSggFOMvGD-i2PnQoIdo5DBEuBEOhe-9zSxm-6AwSewCeTkUA5MhGlBrFBsFSjNBr_eqIs2eNoryuF0my-TtqT1qY-Na3arXn2Q6VjHdmir0Pq6-CxEHsqZotsTz4Fx_OPLsUO5fKxBM36yc_TjUPXPZuslE4orPVawvHP9ZqBrLXFkN46j_2Uv5psQ6CdRtwZ3gQfzDh9rI3Saz5hvh3lQ8N0U_HEirxkK0XyhzoQ2Npi9FeTie6FMUchyXoXw2YAmqGwkGpaY62cM1NCgSKOxyk-mwp-9X7VwKIFfpv6iSa8A0m10zfpPoNgLhyMXf9ribGSqRdOYfD9gGFILmkONALsf722pSSIuXp62UiJEulCGxtGwZjE0RowaX7I8qlbfIOBaLJqBtlqqnJg1BnRdJHnDbhgReHq-Lz-G1IuED0RLhfXtYHCJspS-CWFNoUzWusU38wpKTpTY0vFX6LvgKexFTrCf3sMYn-Kex6XRV0iIoQ5SHgFwMyVO0GOFH6f_iYi9oQ5ERTSfbVhLzvmm3-QBO00DUkjmqCFoPpKHkCFeOEbb7W3rXa51WYLkxV-oaOLleSEpF06rY0OmJ6tJYHuEA4hO9__5vcIhJNpvbT0xY";
        val httpClient: OkHttpClient.Builder = OkHttpClient().newBuilder()
        httpClient.addInterceptor(Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Accept", "application/json")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        })
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = httpClient
            .addInterceptor(interceptor)
            .callTimeout(2, TimeUnit.MINUTES)
            .connectTimeout(150, TimeUnit.SECONDS)
            .writeTimeout(150, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    fun <S> create(service: Class<S>): S {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(40, TimeUnit.SECONDS)
            .writeTimeout(40, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(service)
    }

}