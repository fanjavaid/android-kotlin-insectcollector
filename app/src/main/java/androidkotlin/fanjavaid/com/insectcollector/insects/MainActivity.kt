package androidkotlin.fanjavaid.com.insectcollector.insects

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.LoaderManager
import android.support.v4.app.ShareCompat
import android.support.v4.content.CursorLoader
import android.support.v4.content.FileProvider
import android.support.v4.content.Loader
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidkotlin.fanjavaid.com.insectcollector.R
import androidkotlin.fanjavaid.com.insectcollector.add_insects.AddInsectActivity
import androidkotlin.fanjavaid.com.insectcollector.data.Insect
import androidkotlin.fanjavaid.com.insectcollector.data.InsectContract
import androidkotlin.fanjavaid.com.insectcollector.insect_details.InsectDetailsActivity
import androidkotlin.fanjavaid.com.insectcollector.settings.SettingsActivity
import androidkotlin.fanjavaid.com.insectcollector.util.Constants
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>, InsectAdapter.OnInsectClickListener {
    override fun viewDetails(insect: Insect) {
        val detailIntent = Intent(this, InsectDetailsActivity::class.java)
        detailIntent.putExtra(InsectDetailsActivity.EXTRA_INSECT, insect)

        startActivity(detailIntent)
    }

    override fun shareInsect(insect: Insect) {
        val imageFile = File(insect.picture)
        val uriToImage = FileProvider.getUriForFile(this, Constants.PHOTO_FILE_PROVIDER,
                imageFile)

        val shareIntent: Intent = ShareCompat.IntentBuilder.from(this)
                .setType("image/${imageFile.extension}")
                .addEmailTo("")
                .setText("${insect.name}\n\n${insect.info}")
                .setSubject(getString(R.string.text_share_subject, insect.name))
                .setStream(uriToImage)
                .intent

        shareIntent.setData(uriToImage)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        if (shareIntent.resolveActivity(packageManager) != null) {
            startActivity(shareIntent)
        }
    }

    private var dataAdapter: InsectAdapter? = null
    private val LOADER_ID: Int = 3092
    private var settingPrefs: SharedPreferences? = null
    private var maxData: Int = 10

    companion object {
        val TAG = MainActivity::class.java.simpleName
        val ARG_SORT = "arg_sort"
        val ARG_FAVORITE = "arg_favorite"
        val ARG_MAX_DATA = "arg_max_data"

        val LIMIT_QUERY = "limit"
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        Log.d(TAG, "onLoaderReset")
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        Log.d(TAG, "onLoadFinished ${data != null} , ${data?.count ?: 0}")

        if (data != null && data.count > 0) {
            recyclerview_insects.visibility = View.VISIBLE
            container.visibility = View.GONE

            dataAdapter?.swapCursor(data)
        } else {
            recyclerview_insects.visibility = View.GONE
            container.visibility = View.VISIBLE
        }

        progress_bar.visibility = View.GONE
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        Log.d(TAG, "\nonCreateLoader")

        var returnedCursorLoader: CursorLoader? = null

        args?.let {
            when {
                args.containsKey(ARG_SORT) -> {
                    val sortBy = args.getInt(ARG_SORT)
                    val sortOrder: String = if (sortBy == Constants.SORT_NAME) {
                        "${InsectContract.InsectEntry.COLUMN_NAME} ${Constants.ASCENDING}"
                    } else {
                        "${InsectContract.InsectEntry.COLUMN_UPDATED} ${Constants.DESCENDING}"
                    }

                    val limitQuery = args.getInt(ARG_MAX_DATA).toString()
                    returnedCursorLoader = CursorLoader (
                            this,
                            InsectContract.InsectEntry.CONTENT_URI.buildUpon().appendQueryParameter(LIMIT_QUERY, limitQuery).build(),
                            null,
                            null,
                            null,
                            sortOrder
                    )

                }

                args.containsKey(ARG_FAVORITE) -> {
                    val selection = "${InsectContract.InsectEntry.COLUMN_IS_FAVORITE} = ?"
                    val selectionArgs = arrayOf("${1}")

                    val limitQuery = args.getInt(ARG_MAX_DATA).toString()
                    returnedCursorLoader = CursorLoader (
                            this,
                            InsectContract.InsectEntry.CONTENT_URI.buildUpon().appendQueryParameter(LIMIT_QUERY, limitQuery).build(),
                            null,
                            selection,
                            selectionArgs,
                            null
                    )

                }

                else -> {
                    throw IllegalArgumentException("Invalid arguments")
                }
            }
        }

        return returnedCursorLoader!!
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab_add_new_insect.setOnClickListener{
            val addNewInsectIntent =  Intent(this, AddInsectActivity::class.java)
            startActivity(addNewInsectIntent)
        }

        dataAdapter = InsectAdapter(this, null, this)
        recyclerview_insects.adapter = dataAdapter
        recyclerview_insects.setHasFixedSize(true)
        recyclerview_insects.layoutManager = LinearLayoutManager(this)

        settingPrefs = PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onResume() {
        super.onResume()

        settingPrefs?.let {
            maxData = settingPrefs!!.getString(getString(R.string.key_pref_listnumers), "$maxData").toInt()
        }

        val loaderArgs = Bundle()
        loaderArgs.putInt(ARG_SORT, Constants.SORT_NAME)
        loaderArgs.putInt(ARG_MAX_DATA, maxData)
        supportLoaderManager.restartLoader(LOADER_ID, loaderArgs, this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_sort -> {
                val alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setItems(R.array.text_main_sort_menu, {
                    _: DialogInterface, index: Int ->

                    val loaderArgs = Bundle()
                    when (index) {
                        0 -> {
                            loaderArgs.putInt(ARG_SORT, Constants.SORT_NAME)
                            loaderArgs.putInt(ARG_MAX_DATA, maxData)
                            Snackbar.make(root_view, getString(R.string.text_main_insect_sorted_by_name),
                                    Snackbar.LENGTH_SHORT).show()
                        }

                        1 -> {
                            loaderArgs.putInt(ARG_SORT, Constants.SORT_DATE)
                            loaderArgs.putInt(ARG_MAX_DATA, maxData)
                            Snackbar.make(root_view, getString(R.string.text_main_insect_sorted_by_last_update),
                                    Snackbar.LENGTH_SHORT).show()
                        }
                    }

                    supportLoaderManager.restartLoader(LOADER_ID, loaderArgs, this)
                    supportActionBar?.title = getString(R.string.app_name)
                })
                val sortDialog: AlertDialog = alertBuilder.create()
                sortDialog.show()
            }

            R.id.action_favorite -> {
                // Run Loader to accept Favorite arguments
                var loaderArgs = Bundle()
                loaderArgs.putBoolean(ARG_FAVORITE, true)
                loaderArgs.putInt(ARG_MAX_DATA, maxData)
                supportLoaderManager.restartLoader(LOADER_ID, loaderArgs, this)
                supportActionBar?.title = getString(R.string.text_title_favorite)
            }

            R.id.action_setting -> {
                // Start setting intent
                val settingIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingIntent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
