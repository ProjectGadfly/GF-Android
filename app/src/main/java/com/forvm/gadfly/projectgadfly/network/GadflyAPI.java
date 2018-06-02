package com.forvm.gadfly.projectgadfly.network;

import com.forvm.gadfly.projectgadfly.data.deleteTicketResponse;
import com.forvm.gadfly.projectgadfly.data.getRepsResponse;
import com.forvm.gadfly.projectgadfly.data.getScriptIDResponse;
import com.forvm.gadfly.projectgadfly.data.getScriptResponse;
import com.forvm.gadfly.projectgadfly.data.postScriptResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GadflyAPI {
    @Headers("APIKey: v1key")
    @GET("representatives")
    Call<getRepsResponse> getReps(@Query("address") String address);

    @Headers("APIKey: v1key")
    @GET("id")
    Call<getScriptIDResponse> getScriptID(@Query("ticket") String ticket);

    @Headers("APIKey: v1key")
    @GET("script")
    Call<getScriptResponse> getScript(@Query("id") Integer id);

    @Headers("APIKey: v1key")
    @FormUrlEncoded
    @POST("script")
    Call<postScriptResponse> postScript(@Field("title") String title,
                                        @Field("content") String content,
                                        @Field("tags")List<Integer> tags);

    @Headers("APIKey: v1key")
    @DELETE("script")
    Call<deleteTicketResponse> deleteTicket(@Query("ticket") String ticket);
}
