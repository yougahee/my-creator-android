package com.crecrew.crecre.Network

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApplicationController : Application() {

    private val baseURL = "http://13.125.32.90:3000/api/"
    lateinit var communityNetworkService: CommunityNetworkService

    companion object{
        lateinit var instance : ApplicationController
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        buildNetwork()
    }

    fun buildNetwork() {
        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        communityNetworkService = retrofit.create(CommunityNetworkService::class.java)
    }



}