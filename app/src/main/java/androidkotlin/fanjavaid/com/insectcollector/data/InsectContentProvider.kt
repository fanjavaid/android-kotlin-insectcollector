package androidkotlin.fanjavaid.com.insectcollector.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri

/**
 * Created by fanjavaid on 10/22/17.
 *
 * Define supported uri code to access data
 */
class InsectContentProvider : ContentProvider() {
    var sqlHelper: InsectSqlHelper? = null

    companion object {
        val CODE_ALL: Int = 100
        val CODE_WITH_ID: Int = 101
        private val uriMatcher: UriMatcher = buildUriMatcher()

        fun buildUriMatcher(): UriMatcher {
            val matcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority: String = InsectContract.CONTENT_AUTHORITY

            matcher.addURI(authority, InsectContract.PATH_INSECT, CODE_ALL)
            matcher.addURI(authority, "${InsectContract.PATH_INSECT}/#", CODE_WITH_ID)

            return matcher
        }
    }

    override fun onCreate(): Boolean {
        sqlHelper = InsectSqlHelper(context)

        return true
    }

    override fun getType(p0: Uri?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insert(uri: Uri?, contentValues: ContentValues?): Uri? {
        when (uriMatcher.match(uri)) {
            CODE_ALL -> {
                sqlHelper?.writableDatabase?.insert(
                        InsectContract.InsectEntry.TABLE_NAME,
                        null,
                        contentValues
                )
            }
            else -> throw IllegalArgumentException("Unknown Uri $uri")
        }

        return uri
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?,
                       selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        var cursor: Cursor?

        when (uriMatcher.match(uri)) {
            CODE_ALL -> {
                val limit = uri?.getQueryParameter("limit") ?: "0"

                val queryBuilder = SQLiteQueryBuilder()
                queryBuilder.tables = InsectContract.InsectEntry.TABLE_NAME

                cursor = queryBuilder.query(
                        sqlHelper?.readableDatabase,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        limit
                )
            }

            CODE_WITH_ID -> {
                cursor = sqlHelper?.readableDatabase?.query(
                        InsectContract.InsectEntry.TABLE_NAME,
                        null,
                        "${InsectContract.InsectEntry._ID} = ?",
                        arrayOf("${uri?.lastPathSegment}"),
                        null,
                        null,
                        null
                )
            }

            else -> throw IllegalArgumentException("Unknown Uri $uri")
        }

        cursor?.setNotificationUri(context.contentResolver, uri)

        return cursor
    }

    override fun update(uri: Uri?, contentValues: ContentValues?, where: String?, whereArgs: Array<out String>?): Int {
        val returnValue: Int?

        when (uriMatcher.match(uri)) {
            CODE_WITH_ID -> {
                val dataWhere = "${InsectContract.InsectEntry._ID} = ?"
                val dataWhereArgs = arrayOf("${uri?.lastPathSegment}")

                returnValue = sqlHelper?.writableDatabase?.update(
                        InsectContract.InsectEntry.TABLE_NAME,
                        contentValues,
                        dataWhere,
                        dataWhereArgs
                )
            }

            else -> throw IllegalArgumentException("Unknown Uri $uri")
        }

        return returnValue ?: -1
    }

    override fun delete(uri: Uri?, where: String?, whereArgs: Array<out String>?): Int {
        val returnValue: Int?

        when (uriMatcher.match(uri)) {
            CODE_WITH_ID -> {
                val dataWhere = "${InsectContract.InsectEntry._ID} = ?"
                val dataWhereArgs = arrayOf(uri?.lastPathSegment)

                returnValue = sqlHelper?.writableDatabase?.delete(
                        InsectContract.InsectEntry.TABLE_NAME,
                        dataWhere,
                        dataWhereArgs
                )
            }

            else -> throw IllegalArgumentException("Unknown Uri $uri")
        }

        return returnValue ?: -1
    }
}