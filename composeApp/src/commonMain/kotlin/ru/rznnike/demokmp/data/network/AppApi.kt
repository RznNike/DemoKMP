package ru.rznnike.demokmp.data.network

import de.jensklingenberg.ktorfit.http.GET
import ru.rznnike.demokmp.data.network.model.RandomImageLinkModel

interface AppApi {
    @GET("breeds/image/random")
    suspend fun getRandomImage(): RandomImageLinkModel
}