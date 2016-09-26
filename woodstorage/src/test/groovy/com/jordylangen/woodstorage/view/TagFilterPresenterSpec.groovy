package com.jordylangen.woodstorage.view

import android.content.Context
import com.jordylangen.woodstorage.*
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject

class TagFilterPresenterSpec extends RxSpecification {

    Storage storage
    TagFilterContract.View view
    TagFilterPresenter presenter

    def setup() {
        storage = Mock(Storage)
        view = Mock(TagFilterContract.View)
        presenter = new TagFilterPresenter()

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
        1 * storage.load() >> Flowable.empty()
        0 * view._
    }

    def "should subscribe to the log entries to create selectable tags upon setup"() {
        given:
        def logs = [
                new LogEntry("MyActivity", 1, "onCreate of Activity"),
                new LogEntry("MyService", 1, "onCreate of MyService"),
                new LogEntry("MyFragment", 1, "onCreate of MyFragment"),
                new LogEntry("MyFragment", 1, "onStop of MyFragment"),
                new LogEntry("MyService", 1, "onDestroy of MyService"),
        ]

        storage.load() >> Flowable.fromIterable(logs)

        when:
        presenter.setup(view)

        then:
        3 * view.add(_ as SelectableTag)
    }

    def "should set all selectable tags if already present upon setup"() {
        given:
        def logs = [
                new LogEntry("MyActivity", 1, "onCreate of Activity"),
                new LogEntry("MyService", 1, "onCreate of MyService"),
                new LogEntry("MyFragment", 1, "onCreate of MyFragment"),
                new LogEntry("MyFragment", 1, "onStop of MyFragment"),
                new LogEntry("MyService", 1, "onDestroy of MyService"),
        ]

        storage.load() >> Flowable.fromIterable(logs)

        when:
        presenter.setup(view)

        then:
        3 * view.add(_ as SelectableTag)

        when:
        presenter.setup(view)

        then:
        1 * view.set(_ as List<SelectableTag>)
    }

    def "should update when a tags selected state changed"() {
        given:
        def logs = [
                new LogEntry("MyService", 1, "onCreate of MyService")
        ]

        storage.load() >> Flowable.fromIterable(logs)
        presenter.setup(view)

        when:
        List<SelectableTag> tags = null;
        presenter.observeSelectedTags().subscribe(new Consumer<List<SelectableTag>>() {
            @Override
            void accept(List<SelectableTag> selectableTags) {
                tags = selectableTags
            }
        })

        and:
        presenter.tagSelectedChanged(new SelectableTag("MyService", false), false)

        then:
        tags != null
        tags[0].tag == "MyService"
        !tags[0].isSelected()
    }

    def "should call onCompleted and unsubscribe upon teardown"() {
        given:
        def logEntriesSubscription = Mock(Disposable)
        def selectedTagsPublishSubject = PublishSubject.create()
        presenter.logEntriesSubscription = logEntriesSubscription
        presenter.selectedTagsPublishSubject = selectedTagsPublishSubject

        when:
        presenter.teardown()

        then:
        selectedTagsPublishSubject.hasComplete()
        1 * logEntriesSubscription.isDisposed()
        1 * logEntriesSubscription.dispose()
    }
}