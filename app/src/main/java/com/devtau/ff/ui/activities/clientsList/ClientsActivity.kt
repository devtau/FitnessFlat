package com.devtau.ff.ui.activities.clientsList

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.devtau.ff.R
import com.devtau.ff.adapters.CustomLinearLayoutManager
import com.devtau.ff.adapters.ClientsAdapter
import com.devtau.ff.data.model.Client
import com.devtau.ff.ui.DependencyRegistry
import com.devtau.ff.ui.activities.DBViewerActivity
import com.devtau.ff.ui.activities.clientDetails.ClientDetailsActivity
import com.devtau.ff.util.AppUtils
import com.devtau.ff.util.Constants
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_clients.*

class ClientsActivity: AppCompatActivity(), ClientsView {

    var presenter: ClientsPresenterImpl? = null
    private var adapter: ClientsAdapter? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clients)
        DependencyRegistry().inject(this)
        AppUtils.initToolbar(this, R.string.clients, false)
        initUi()
        initList()
    }

    override fun onStart() {
        super.onStart()
        presenter?.restartLoaders()
    }

    override fun onStop() {
        super.onStop()
        presenter?.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_clients, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.openDB -> {
            DBViewerActivity.newInstance(this)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    //</editor-fold>


    //<editor-fold desc="View overrides">
    override fun showMsg(msgId: Int, confirmedListener: Action?) = showMsg(getString(msgId, confirmedListener))
    override fun showMsg(msg: String, confirmedListener: Action?) = AppUtils.alertD(LOG_TAG, msg, this, confirmedListener)

    override fun updateClients(list: List<Client>?) = adapter?.setList(list)
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initUi() {
        listView?.postDelayed({ fab.show() }, Constants.STANDARD_DELAY_MS)
        fab.setOnClickListener { ClientDetailsActivity.newInstance(this, null) }
    }

    private fun initList() {
        adapter = ClientsAdapter(presenter?.clients, Consumer { ClientDetailsActivity.newInstance(this, it.id) })
        listView?.layoutManager = CustomLinearLayoutManager(this)
        listView?.adapter = adapter
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "ClientsActivity"
    }
}