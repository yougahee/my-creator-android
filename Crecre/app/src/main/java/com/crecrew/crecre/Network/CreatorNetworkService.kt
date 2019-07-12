package com.crecrew.crecre.Network

import android.util.Log
import com.crecrew.crecre.Network.Get.GetCreatorNum
import com.crecrew.crecre.Network.Get.GetCreatorSearch
import com.crecrew.crecre.Network.Get.GetCreatorTodayHotRank
import com.crecrew.crecre.Network.Get.GetProfileResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CreatorNetworkService {

    @GET("creators/allCreatorCnt")
    fun getCreatorNum(
        ): Call<GetCreatorNum>

    @GET("creators/creatorSearch")
    fun getCreatorSearch(
        @Query("name") name: String
        ): Call<GetCreatorSearch>

    @GET("creators/chart/hot")
    fun getCreatorTodayHotRank(
    ): Call<GetCreatorTodayHotRank>

    @GET("creators/profiles/{creatorIdx}")
    fun getProfileResponse(
        @Path("creatorIdx") creatorIdx : Int
    ): Call<GetProfileResponse>
}

