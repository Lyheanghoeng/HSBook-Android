package com.hsbook.Retrofit.Interface;

import com.hsbook.model.BookResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface HomeProfileService {

    String UPDATE_MEMBER_PROFILE = "app.php?f=0";

    @GET(UPDATE_MEMBER_PROFILE)
    Call<List<BookResponse>> getBookList();

    @GET
    Call<ResponseBody> downlload(@Url String fileUrl);

    @GET("app.php")
    Call<List<BookResponse>> getSearchBook(@Query("app_kw_kh") String param);
}
