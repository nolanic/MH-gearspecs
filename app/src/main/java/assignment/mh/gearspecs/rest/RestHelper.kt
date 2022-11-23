package assignment.mh.gearspecs.rest

import assignment.mh.gearspecs.rest.models.ArmorUnit
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.reflect.KFunction

object RestHelper {
    private val mhwdbApi : MhwdbApi
    private val callsInProgress = mutableSetOf<BaseCallback<*>>()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://mhw-db.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mhwdbApi = retrofit.create(MhwdbApi::class.java)
    }

    private fun getCallInProgress(apiDescription:String) : BaseCallback<*>? {
        for (baseCallback in callsInProgress) {
            if (baseCallback.apiDescription == apiDescription) {
                return baseCallback
            }
        }
        return null
    }

    private fun <T>invokeApiCall(apiDescription: String, clientCallback : Callback<T>, apiFunction : KFunction<Call<T>>, vararg apiParameters : Any) {
        val baseCallbackInProgress = getCallInProgress(apiDescription)
        if (baseCallbackInProgress != null) {
            baseCallbackInProgress.addClient(clientCallback)
            return
        }

        val baseCallback = BaseCallback(apiDescription, clientCallback)
        callsInProgress.add(baseCallback)
        apiFunction.call(*apiParameters).enqueue(baseCallback)
    }

    //This method is doing the same thing as getArmorUnits() but with way less boilerplate code.
    //Future implementations of additional API calls could be done this way
    fun getArmorUnits2(callback: Callback<List<ArmorUnit>>) {
        val apiDescription = "getArmorUnits"
        invokeApiCall(apiDescription, callback, mhwdbApi::getArmourData)
    }

    fun getArmorUnits(callback: Callback<List<ArmorUnit>>) {
        val apiDescription = "getArmorUnits"

        val baseCallbackInProgress = getCallInProgress(apiDescription)
        if (baseCallbackInProgress != null) {
            baseCallbackInProgress.addClient(callback)
            return
        }

        val baseCallback = BaseCallback(apiDescription, callback)
        callsInProgress.add(baseCallback)
        mhwdbApi.getArmourData().enqueue(baseCallback)
    }

    private class BaseCallback<T> : retrofit2.Callback<T> {
        val apiDescription : String
        val clientCallbacks = mutableSetOf<Callback<T>>()

        constructor(apiDescription: String, callback: Callback<T>) {
            this.apiDescription = apiDescription;
            clientCallbacks.add(callback)
        }

        private fun notifyClientsAndDispose(result: T?, error: Throwable?) {
            for (callback in clientCallbacks) {
                callback.onResponse(result, error)
            }
            callsInProgress.remove(this)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            val result = response.body()
            if (result != null) {
                notifyClientsAndDispose(result, null)
                return
            }

            val error = response.errorBody()
            if (error != null) {
                val responseError = Throwable(error.toString())
                notifyClientsAndDispose(null, responseError)
                return
            }

            for (callback in clientCallbacks) {
                val error = Throwable("God knows what happened")
                notifyClientsAndDispose(null, error)
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            notifyClientsAndDispose(null, t)
        }

        fun addClient(callback : Callback<*>) {
            clientCallbacks.add(callback as Callback<T>)
        }
    }

    interface Callback<T> {
        fun onResponse(result:T?, error:Throwable?)
    }
}