package ru.rznnike.demokmp.app.dispatcher.event

import kotlinx.coroutines.launch
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import kotlin.reflect.KClass

class EventDispatcher(
    private val coroutineScopeProvider: CoroutineScopeProvider
) {
    private val eventListeners = mutableMapOf<String, MutableSet<EventListener>>()

    fun addEventListener(appEventClass: KClass<out AppEvent>, listener: EventListener) {
        val key = appEventClass.java.name
        if (eventListeners[key] == null) {
            eventListeners[key] = mutableSetOf()
        }

        val list = eventListeners[key]
        list?.add(listener)
    }

    fun addEventListener(appEventClasses: List<KClass<out AppEvent>>, listener: EventListener) =
        appEventClasses.forEach {
            addEventListener(it, listener)
        }

    fun addSingleByTagEventListener(appEventClass: KClass<out AppEvent>, listener: EventListener) {
        val key = appEventClass.java.name
        if (eventListeners[key] == null) {
            eventListeners[key] = mutableSetOf()
        }

        val list = eventListeners[key]
        list?.removeAll { it.getTag() == listener.getTag() }
        list?.add(listener)
    }

    fun removeEventListener(listener: EventListener) = eventListeners
        .filter { it.value.size > 0 }
        .forEach { it.value.remove(listener) }

    fun sendEvent(appEvent: AppEvent) {
        val key = appEvent::class.java.name
        eventListeners[key]?.forEach { listener ->
            coroutineScopeProvider.ui.launch {
                listener.onEvent(appEvent)
            }
        }
    }

    interface EventListener {
        fun onEvent(event: AppEvent)

        fun getTag(): String = this::class.java.name
    }
}