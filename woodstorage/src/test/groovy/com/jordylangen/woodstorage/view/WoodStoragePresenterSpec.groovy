package com.jordylangen.woodstorage.view

import com.jordylangen.woodstorage.*
import com.jordylangen.woodstorage.storage.LogEntry
import com.jordylangen.woodstorage.storage.Storage
import com.jordylangen.woodstorage.storage.StorageFactory
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class WoodStoragePresenterSpec extends RxSpecification {

    Storage storage
    WoodStorageContract.View view
    WoodStoragePresenter presenter

    def setup() {
        storage = Mock(Storage)
        view = Mock(WoodStorageContract.View)
        presenter = new WoodStoragePresenter()

        WoodStorageFactory.getInstance(Mock(File), new StorageFactory() {
            @Override
            Storage create(File directory) {
                return storage
            }
        })
    }

    def "should subscribe on setup"() {
        when:
        presenter.setup(view)

        then:
        1 * storage.load() >> Flowable.empty()
        0 * view._
    }

    def "should add all logs to the view in normal order"() {
        given:
        def count = 10
        def logs = []

        for (def index = 0; index < count; index++) {
            logs.add(new LogEntry("spec", 0, Integer.toString(index)))
        }

        def observable = Flowable.fromIterable(logs)
        storage.load() >> observable

        when:
        presenter.setup(view)

        then:
        count * view.add(_ as LogEntry)
    }

    def "should clear all logs upon sort order inversion and add them again inverted"() {
        given:
        def count = 10
        def logs = []

        for (def index = 0; index < count; index++) {
            logs.add(new LogEntry("spec", 0, Integer.toString(index)))
        }

        def observable = Flowable.fromIterable(logs)
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
        1 * storage.load() >> Flowable.empty()

        when:
        presenter.onOptionsItemSelected(R.id.woodstorage_action_clear)

        then:
        1 * storage.load() >> Flowable.empty()
        1 * storage.clear()
        1 * view.clear()
    }

    def "should unsubscribe all subscriptions upon teardown"() {
        given:
        def logEntriesSubscription = Mock(Disposable)
        def selectedTagsSubscription = Mock(Disposable)
        presenter.logEntriesSubscription = logEntriesSubscription
        presenter.selectedTagsSubscription = selectedTagsSubscription

        when:
        presenter.teardown()

        then:
        1 * logEntriesSubscription.isDisposed()
        1 * logEntriesSubscription.dispose()
        1 * selectedTagsSubscription.isDisposed()
        1 * selectedTagsSubscription.dispose()
    }

    def "should show the tag filter dialog and filter the logs when filters are applied"() {
        given:
        def selectableTags = [
                new SelectableTag("MyActivity", false),
                new SelectableTag("MyService", true),
                new SelectableTag("MyFragment", false)
        ]

        def tagFilterPresenter = Mock(TagFilterContract.Presenter)
        tagFilterPresenter.observeSelectedTags() >> Observable.fromIterable(selectableTags).toList().toObservable()

        def logs = [
                new LogEntry("MyActivity", 1, "onCreate of Activity"),
                new LogEntry("MyService", 1, "onCreate of MyService"),
                new LogEntry("MyFragment", 1, "onCreate of MyFragment")
        ]

        storage.load() >> Flowable.fromIterable(logs)

        PresenterCache.put(R.id.dialog_tag_filter, tagFilterPresenter)

        when:
        presenter.setup(view)

        then:
        3 * view.add(_ as LogEntry)

        when:
        presenter.onOptionsItemSelected(R.id.woodstorage_action_filter)

        then:
        1 * view.showTagFilterDialog()
        1 * view.clear()
        1 * view.add(_ as LogEntry) >> { LogEntry logEntry ->
            assert logEntry.tag == "MyService"
        }
    }
}