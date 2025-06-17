package ru.rznnike.demokmp.app.navigation.navtype

import ru.rznnike.demokmp.domain.log.LogNetworkMessage
import kotlin.reflect.typeOf

val logNetworkMessageNavType = typeOf<LogNetworkMessage>() to jsonNavTypeOf<LogNetworkMessage>()
//val someNullableNavType = typeOf<SomeClass?>() to jsonNavTypeOf<SomeClass?>(isNullableAllowed = true)
//val someEnumNavType = typeOf<SomeEnum>() to enumNavTypeOf<SomeEnum>()
//val someListNavType = typeOf<List<SomeItem>>() to jsonNavTypeOf<List<SomeItem>>()