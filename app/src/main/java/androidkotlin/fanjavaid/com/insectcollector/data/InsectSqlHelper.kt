package androidkotlin.fanjavaid.com.insectcollector.data

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.content.Context as Ctx

/**
 * Created by fanjavaid on 10/22/17.
 */
class InsectSqlHelper(context: Ctx?,
                      name: String = DATABASE_NAME ,
                      factory: SQLiteDatabase.CursorFactory? = null,
                      version: Int = DATABASE_VERSION) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        val DATABASE_NAME: String = "insect_collector.db"
        val DATABASE_VERSION: Int = 3
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query: String = """
            CREATE TABLE ${InsectContract.InsectEntry.TABLE_NAME} (
                ${InsectContract.InsectEntry._ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                ${InsectContract.InsectEntry.COLUMN_NAME} TEXT NOT NULL,
                ${InsectContract.InsectEntry.COLUMN_DESCRIPTION} TEXT NOT NULL,
                ${InsectContract.InsectEntry.COLUMN_PHOTO} TEXT NOT NULL,
                ${InsectContract.InsectEntry.COLUMN_CREATED} DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                ${InsectContract.InsectEntry.COLUMN_UPDATED} DATETIME
            );
        """.trimIndent()

        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        Log.d("InsectSqlHelper", "onUpgrade executed!")

//        db?.execSQL("DROP TABLE IF EXISTS ${InsectContract.InsectEntry.TABLE_NAME}")
        db?.let {
            if (db.version < DATABASE_VERSION) {
                db.execSQL("""ALTER TABLE ${InsectContract.InsectEntry.TABLE_NAME}
                    |ADD ${InsectContract.InsectEntry.COLUMN_IS_FAVORITE}
                    |INTEGER NOT NULL DEFAULT 0""".trimMargin())
            }
        }
    }
}