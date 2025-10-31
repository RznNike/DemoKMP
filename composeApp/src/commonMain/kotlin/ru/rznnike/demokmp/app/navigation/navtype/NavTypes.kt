package ru.rznnike.demokmp.app.navigation.navtype

import ru.rznnike.demokmp.domain.log.NetworkLogMessage
import kotlin.reflect.typeOf

val networkLogMessageNavType = typeOf<NetworkLogMessage>() to jsonNavTypeOf<NetworkLogMessage>()
//val someNullableNavType = typeOf<SomeClass?>() to jsonNavTypeOf<SomeClass?>(isNullableAllowed = true)
//val someEnumNavType = typeOf<SomeEnum>() to enumNavTypeOf<SomeEnum>()
//val someListNavType = typeOf<List<SomeItem>>() to jsonNavTypeOf<List<SomeItem>>()