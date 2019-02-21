package com.jordylangen.woodstorage.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.jordylangen.woodstorage.R

class WoodStorageViewActivity : AppCompatActivity() {

    private var woodStoragePresenter: WoodStorageContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        woodStoragePresenter = PresenterCache[R.id.view_wood_storage]
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.overview_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        woodStoragePresenter!!.onOptionsItemSelected(item.itemId)
        return true
    }
}
