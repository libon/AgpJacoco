package com.example.agpjacoco

class Hello {
    private val listeners = mutableListOf<Listener>()
    interface Listener {
        fun onSomething()
    }
    fun hello() {
        listeners.forEach { it.onSomething() }
    }
}