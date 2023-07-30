package techare.simplevms.sensor.kind

import foundation.utils.RawStringStyle
import foundation.utils.s

interface Sensor {

    @JvmInline
    value class Location(private val name: String) {
        init {
            require(name.isNotEmpty()) { "Empty location is not allowed" }
            require(name.matches(validationRegex)) {
                """ |Location name can only contain lower case letters, numbers, and the following 
                    |symbols: underscore, dash, dots. Further a location can name cannot start 
                    |with a number.""".s(RawStringStyle.Margin)
            }
        }

        override fun toString(): String = name

        companion object {
            val validationRegex = Regex("[a-z-._]+[0-9]*")
        }
    }

    val location: Location
    val serialNo: String
    val description: String

}

