package androidkotlin.fanjavaid.com.insectcollector.insects

import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.support.annotation.Nullable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import androidkotlin.fanjavaid.com.insectcollector.R
import androidkotlin.fanjavaid.com.insectcollector.data.Insect
import androidkotlin.fanjavaid.com.insectcollector.data.InsectContract
import androidkotlin.fanjavaid.com.insectcollector.extension.InsectExtension.Companion.asInsectData
import androidkotlin.fanjavaid.com.insectcollector.util.GlideApp
import kotlinx.android.synthetic.main.item_insect.view.*


/**
 * Created by fanjavaid on 10/22/17.
 */
class InsectAdapter(var context: Context?, @Nullable var cursor: Cursor?, var onInsectListener: OnInsectClickListener) : RecyclerView.Adapter<InsectAdapter.InsectViewHolder>() {
    override fun onBindViewHolder(holder: InsectViewHolder?, position: Int) {
        cursor?.moveToFirst()
        cursor?.moveToPosition(position)

        val view: View? = holder?.itemView

        view?.text_insect_name?.text = cursor?.getColumnIndex(InsectContract.InsectEntry.COLUMN_NAME)?.let { cursor?.getString(it) }
        view?.text_insect_info?.text = cursor?.getColumnIndex(InsectContract.InsectEntry.COLUMN_DESCRIPTION)?.let { cursor?.getString(it) }

        val file: String? = cursor?.getColumnIndex(InsectContract.InsectEntry.COLUMN_PHOTO)?.let { cursor?.getString(it) }
        GlideApp.with(context)
                .load(file)
                .error(R.drawable.ic_art_placeholder)
                .override(500, 300)
                .into(view?.image_insect)

        view?.button_detail_insect?.setOnClickListener {
            cursor?.moveToPosition(position)
            onInsectListener.viewDetails(cursor!!.asInsectData())
        }

        view?.button_share_insect?.setOnClickListener {
            cursor?.moveToPosition(position)
            onInsectListener.shareInsect(cursor!!.asInsectData())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): InsectViewHolder {
        val inflater = from(context)
        val itemLayout = R.layout.item_insect
        val view = inflater.inflate(itemLayout, parent, false)

        return InsectViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    fun swapCursor(data: Cursor?) {
        Log.d("InsectAdapter", DatabaseUtils.dumpCursorToString(data))
        cursor = data
        this.notifyDataSetChanged()
    }

    inner class InsectViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

    interface OnInsectClickListener {
        fun viewDetails(insect: Insect)
        fun shareInsect(insect: Insect)
    }
}