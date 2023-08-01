package foundation.observable

fun interface Observer<in T> {
    fun subjectChanged(subject: T)
}

infix fun Observer<Observable<Observable<*>>>.isObserving(subject: Observable<Observable<*>>): Boolean {
    return subject.isObservedBy(this)
}