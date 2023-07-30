package foundation.observable.doodads

import foundation.observable.AbstractObservable

class Light(private val color:String) : AbstractObservable<Light>() {
    var isOn: Boolean = false; private set

    fun toggle() {
        isOn = !isOn
        notifyObservers()
    }

    fun switchOn() {
        if (!isOn) toggle()
    }

    fun switchOff() {
        if (isOn) toggle()
    }

    override fun toString(): String {
        return "Light(color='$color', isOn=$isOn)"
    }


}