package com.kyuwankim.android.remoteretrofit;

import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by kimkyuwan on 2017. 3. 7..
 */

public interface SeoulOpenService {

    @GET("7a79575a4a6c706e31384a62724a69/json/SearchParkingInfoRealtime/1/10/강남구")
    Call<List<Repo>> listRepos(@Path("user") String user);


}
