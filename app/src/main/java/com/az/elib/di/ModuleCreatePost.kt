package com.az.elib.di

import android.content.Context
import android.util.Log
import com.az.elib.data.datasource.FirebaseFCM
import com.az.elib.data.datasource.FirebaseFirestoreCreatePost
import com.az.elib.data.datasource.retrofit.RetrofitCloudMessagingAPI
import com.az.elib.data.local.CredentialsProvider
import com.az.elib.data.local.MySharedPreferences
import com.az.elib.data.repository.RepositoryCreatePost
import com.az.elib.domain.usecases.postfeed.CreatePostUserCase
import com.az.elib.domain.usecases.postfeed.SendNotificationUserCase
import com.google.auth.oauth2.GoogleCredentials
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.Arrays


@Module
@InstallIn(SingletonComponent::class)
object ModuleCreatePost {
    @Throws(IOException::class)
    private fun getAccessToken(context: Context): String {
        val googleCredentials = GoogleCredentials
            .fromStream(CredentialsProvider(context).getCredentials())
            .createScoped(Arrays.asList<String>("https://www.googleapis.com/auth/firebase.messaging"))
        googleCredentials.refresh()
        return googleCredentials.accessToken.tokenValue
    }

    @Provides
    fun provideFirebaseFirestoreCreatePost(): FirebaseFirestoreCreatePost {
        return FirebaseFirestoreCreatePost()
    }

    @Provides
    fun provideRetrofit(context: Context): Retrofit {

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    // TokenManager si a singleton class that stores the access token
                    // once when the app starts
                    .addHeader("Authorization", "Bearer ${getAccessToken(context)}")
                    .addHeader("Content-Type", "application/json; UTF-8")
                    .build()
                chain.proceed(newRequest)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideRetrofitCloudMessagingAPI(retrofit: Retrofit) : RetrofitCloudMessagingAPI {
        return retrofit.create(RetrofitCloudMessagingAPI::class.java)
    }

    @Provides
    fun provideSendNotificationUserCase(retrofitCloudMessagingAPI: RetrofitCloudMessagingAPI) : SendNotificationUserCase {
        return SendNotificationUserCase(FirebaseFCM(), retrofitCloudMessagingAPI)
    }
    @Provides
    fun provideCreatePostUserCase(
        mySharedPreferences: MySharedPreferences,
        repositoryCreatePost: RepositoryCreatePost
    ): CreatePostUserCase {
        return CreatePostUserCase(mySharedPreferences, repositoryCreatePost)
    }
}