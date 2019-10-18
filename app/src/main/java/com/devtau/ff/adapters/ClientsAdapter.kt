package com.devtau.ff.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.devtau.ff.R
import com.devtau.ff.adapters.viewHolders.ClientsViewHolder
import com.devtau.ff.rest.model.Client
import com.devtau.ff.util.Logger
import io.reactivex.functions.Consumer

class ClientsAdapter(
    private var clients: List<Client>?,
    private val listener: Consumer<Client>
): RecyclerView.Adapter<ClientsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientsViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_client, parent, false)
        return ClientsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientsViewHolder, position: Int) {
        val client = clients?.get(position) ?: return
        Logger.d(LOG_TAG, "onBindViewHolder. client=$client")
        Glide.with(holder.context).load(
            if (!TextUtils.isEmpty(client.avatarUrl)) client.avatarUrl
            else if (client.avatarId != null) client.avatarId
            else null)
            .transition(DrawableTransitionOptions.withCrossFade()).into(holder.image)
        holder.name.text = client.getName()
        holder.root.setOnClickListener { listener.accept(client) }
    }

    override fun getItemCount(): Int = clients?.size ?: 0


    fun setList(list: List<Client>?) {
        this.clients = list
        notifyDataSetChanged()
    }


    companion object {
        private const val LOG_TAG = "ClientsAdapter"
    }
}