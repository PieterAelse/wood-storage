package com.jordylangen.woodstorage.view

import android.content.Context
import com.jordylangen.woodstorage.*
import rx.Observable

class WoodStoragePresenterSpec extends RxSpecification {

    Storage storage
    WoodStorageContract.View view
    WoodStoragePresenter presenter

    def setup() {
        storage = Mock(Storage)
        view = Mock(WoodStorageContract.View)
        presenter = new WoodStoragePresenter()

        WoodStorageFactory.getInstance(null, new StorageFactory() {
            @Override
            Storage create(Context context) {
                return storage
            }
        })
    }

    def "should subscribe on setup"() {
        when:
        presenter.setup(view)

        then:
        1 * storage.load() >> Observable.empty()
        0 * view._
    }

    def "should add all logs to the view in normal order"() {
        given:
        def count = 10;
        def logs = []

        for (def index = 0; index < count; index++) {
            logs.add(new LogEntry("spec", 0, Integer.toString(index)))
        }

        def observable = Observable.from(logs)
        storage.load() >> observable

        when:
        presenter.setup(view)

        then:
        count * view.add(_ as LogEntry)
    }

    def "should clear all logs upon sort order inversion and add them again inverted"() {
        given:
        def count = 10;
        def logs = []

        for (def index = 0; index < count; index++) {
            logs.add(new LogEntry("spec", 0, Integer.toString(index)))
        }

        def observable = Observable.from(logs)
        storage.load() >> observable

        when:
        presenter.setup(view)

        then:
        count * view.add(_ as LogEntry)

        when:
        presenter.onOptionsItemSelected(R.id.woodstorage_action_sort)

        then:
        1 * view.clear()
        count * view.addAt(_ as LogEntry, 0)
    }

    def "should clear logs"() {
        when:
        presenter.setup(view)

        then:
        1 * storage.load() >> Observable.empty()

        when:
        presenter.onOptionsItemSelected(R.id.woodstorage_action_clear)

        then:
        1 * storage.load() >> Observable.empty()
        1 * storage.clear()
        1 * view.clear()
    }
}