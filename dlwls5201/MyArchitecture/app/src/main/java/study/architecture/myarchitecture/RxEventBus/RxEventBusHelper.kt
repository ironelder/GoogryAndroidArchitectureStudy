package study.architecture.myarchitecture.rxeventbus

import android.os.Bundle
import io.reactivex.subjects.PublishSubject

object RxEventBusHelper {

    val mSubject = PublishSubject.create<Bundle>()

    fun sendEvent(bundle: Bundle) {
        mSubject.onNext(bundle)
    }

}