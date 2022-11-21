package assignment.mh.gearspecs.rest

import assignment.mh.gearspecs.rest.models.ArmorUnit
import retrofit2.Call
import retrofit2.http.GET

interface MhwdbApi {

    @GET("/armor")
    fun getArmourData():Call<List<ArmorUnit>>
}