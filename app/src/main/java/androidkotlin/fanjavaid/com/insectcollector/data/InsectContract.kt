package androidkotlin.fanjavaid.com.insectcollector.data

import android.net.Uri
import android.provider.BaseColumns

/**
 * Created by fanjavaid on 10/22/17.
 * Define all global content provider and sql helper constant contract
 */
class InsectContract {
    companion object {
        val CONTENT_AUTHORITY: String = "androidkotlin.fanjavaid.com.insectcollector.CONTENT_AUTHORITY"
        val BASE_CONTENT_URI: Uri = Uri.parse("content://" + CONTENT_AUTHORITY)
        val PATH_INSECT: String = "insect"
    }

    class InsectEntry : BaseColumns {
        companion object {
            val CONTENT_URI: Uri = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_INSECT)
                    .build()

            val TABLE_NAME = "insects"

            val _ID = "id"

            val COLUMN_NAME = "name"
            val COLUMN_DESCRIPTION = "description"
            val COLUMN_PHOTO = "photo"
            val COLUMN_IS_FAVORITE = "is_favorite"

            val COLUMN_CREATED = "created_at"
            val COLUMN_UPDATED = "updated_at"
        }
    }

}