package foundation.pedantic


/**
 * Expecting
 *
 * @param T
 * @param expectedValue
 * @param lazyMessage
 * @receiver
 */
inline fun <T> T.expecting(expectedValue: T, lazyMessage: () -> String) {
    if (this != expectedValue)
        throw IllegalStateException(lazyMessage())
}

/**
 * Expecting
 *
 * @param condition
 * @param lazyMessage
 * @receiver
 */
fun expecting(condition: Boolean, lazyMessage: () -> String) {
    if (!condition)
        throw IllegalStateException(lazyMessage())
}

/**
 * Require
 *
 * @param T
 * @param value
 * @param passed
 * @param failure
 * @receiver
 * @return
 */
fun <T> require(value: T, passed: Boolean, failure: () -> String): T {
    return when {
        passed -> value
        else -> throw IllegalArgumentException(failure())
    }
}

inline fun <reified T, reified U> T.expectStateOfOrFail(raiseUnexpected: (U) -> Nothing): T
        where U : T {

    if (this is U)
        raiseUnexpected(this)

    return this
}