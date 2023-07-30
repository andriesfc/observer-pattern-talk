package foundation.observable

fun interface Observer<in T> {
    fun subjectChanged(subject: T)
}