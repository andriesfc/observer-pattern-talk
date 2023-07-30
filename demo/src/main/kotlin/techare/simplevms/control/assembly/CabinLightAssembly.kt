package techare.simplevms.control.assembly

import foundation.observable.AbstractObservable
import foundation.observable.Observable
import techare.simplevms.control.CabinControl
import techare.simplevms.sensor.kind.CabinLightSensor
import techare.simplevms.sensor.kind.Sensor

class CabinLightAssembly(
    override val location: Sensor.Location,
    override val serialNo: String,
    override val description: String
) : Observable<CabinLightAssembly>, AbstractObservable<CabinLightAssembly>(),
    CabinLightSensor, CabinControl {
    override var state: CabinLightSensor.State = CabinLightSensor.State.OFF; private set(value) {
        if (value != field) {
            field = value
            notifyObservers()
        }
    }

    override fun dimCabin() {
        state = CabinLightSensor.State.ON_DIM
    }
    override fun switchOn() {
        state = CabinLightSensor.State.ON
    }
    override fun switchOff() {
        state = CabinLightSensor.State.OFF
    }
}