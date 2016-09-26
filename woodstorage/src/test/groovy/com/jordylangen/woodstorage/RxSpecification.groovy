package com.jordylangen.woodstorage
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.functions.Function
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import spock.lang.Specification

class RxSpecification extends Specification {

    def setup() {
        RxJavaPlugins.reset()
        RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            Scheduler apply(Scheduler scheduler) throws Exception {
                return Schedulers.trampoline()
            }
        })

        RxAndroidPlugins.reset()
        RxAndroidPlugins.setMainThreadSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            Scheduler apply(Scheduler scheduler) throws Exception {
                return Schedulers.trampoline()
            }
        })
    }
}