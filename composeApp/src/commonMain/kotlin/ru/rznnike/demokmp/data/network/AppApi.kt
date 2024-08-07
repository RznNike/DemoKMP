package ru.rznnike.demokmp.data.network

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import ru.rznnike.demokmp.data.network.model.RandomImageLinksModel

interface AppApi {
    @GET("breeds/image/random/{count}")
    suspend fun getRandomImages(
        @Path("count") count: Int
    ): RandomImageLinksModel
}