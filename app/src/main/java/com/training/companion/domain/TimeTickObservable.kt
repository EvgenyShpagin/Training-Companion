package com.training.companion.domain

interface TimeTickObservable {
    val observers: ArrayList<TimeTickObserver>

    fun add(observer: TimeTickObserver)

    fun remove(observer: TimeTickObserver)

    fun tick() {
        observers.forEach {
            it.onTick()
        }
    }
}