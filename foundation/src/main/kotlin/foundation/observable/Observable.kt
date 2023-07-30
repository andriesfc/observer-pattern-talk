package foundation.observable

interface Observable<out T : Observable<T>> {
    fun attach(observer: Observer<T>)
    fun detach(observer: Observer<T>)
    infix fun observes(observer: Observer<T>): Boolean
    fun detachAll()

    fun isObserved(): Boolean
}