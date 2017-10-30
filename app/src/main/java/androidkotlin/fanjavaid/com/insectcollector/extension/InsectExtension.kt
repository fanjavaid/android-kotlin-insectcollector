package androidkotlin.fanjavaid.com.insectcollector.extension

import android.database.Cursor
import androidkotlin.fanjavaid.com.insectcollector.data.Insect
import androidkotlin.fanjavaid.com.insectcollector.data.InsectContract

/**
 * Created by fanjavaid on 10/29/17.
 */
class InsectExtension {
    companion object {
        // Cool feature in Koltin, using extension
        // Convert cursor to Insect data
        fun Cursor.asInsectData() : Insect {
            val columnId = this.getColumnIndex(InsectContract.InsectEntry._ID)
            val columnName = this.getColumnIndex(InsectContract.InsectEntry.COLUMN_NAME)
            val columnInfo = this.getColumnIndex(InsectContract.InsectEntry.COLUMN_DESCRIPTION)
            val columnPhoto = this.getColumnIndex(InsectContract.InsectEntry.COLUMN_PHOTO)
            val columnCreated = this.getColumnIndex(InsectContract.InsectEntry.COLUMN_CREATED)
            val columnUpdated = this.getColumnIndex(InsectContract.InsectEntry.COLUMN_UPDATED)

            return Insect(
                    this.getInt(columnId),
                    this.getString(columnName),
                    this.getString(columnInfo),
                    this.getString(columnPhoto),
                    this.getString(columnCreated),
                    this.getString(columnUpdated)
            )
        }
    }
}