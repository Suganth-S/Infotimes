package com.suganth.infotimes.api

import com.suganth.infotimes.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        /**
         * lazy - used when you want to access some read-only property because the same object is accessed throughout.
         */
        private val retrofit by lazy {
            /**
             * we use this to debugg and see what's our response has , so that we can able to
             * see which request we are actually making and what the responses are
             * Level.BODY - by setting level at 2nd line, we can able to see the response of the body
             * then we using the interceptor and create a client
             * Finally we get our instance by creating a Retrofit Builder
             */
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build()
        }

        /**
         * this is the actual API object that we willbe able to use
         * from everywhere to make our actual network request
         */

        val api by lazy {
            retrofit.create(NewsApi::class.java)
        }
    }
}