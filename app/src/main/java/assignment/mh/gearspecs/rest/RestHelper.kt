package assignment.mh.gearspecs.rest

import assignment.mh.gearspecs.rest.models.ArmorUnit
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestHelper {
    companion object {
        private val mhwdbApi : MhwdbApi

        init {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://mhw-db.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            mhwdbApi = retrofit.create(MhwdbApi::class.java)
        }

        fun getArmorUnits(callback: Callback<List<ArmorUnit>>) {
            mhwdbApi.getArmourData().enqueue(BaseCallback(callback))
        }
    }

    private class BaseCallback<T>(val callback:Callback<T>) : retrofit2.Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            val result = response.body()
            if (result != null) {
                callback.onResponse(result, null)
                return
            }

            val error = response.errorBody()
            if (error != null) {
                val responseError = Throwable(error.toString())
                callback.onResponse(null, responseError)
                return
            }

            callback.onResponse(null, Throwable("God knows what happened"))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            callback.onResponse(null, t)
        }
    }

    interface Callback<T> {
        fun onResponse(result:T?, error:Throwable?)
    }
}