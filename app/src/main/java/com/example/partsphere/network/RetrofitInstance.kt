package com.example.partsphere.network

import android.content.Context
import com.example.partsphere.utils.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitInstance {

    private const val BASE_URL = "https://overlavishly-rightish-amelia.ngrok-free.dev/" // change this

    @Provides
    @Singleton
    fun provideOkHttpClient(
        prefs: PreferenceManager
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(prefs)) // If you already added interceptor
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideDistributorApi(retrofit: Retrofit): DistributorApi {
        return retrofit.create(DistributorApi::class.java)
    }
}