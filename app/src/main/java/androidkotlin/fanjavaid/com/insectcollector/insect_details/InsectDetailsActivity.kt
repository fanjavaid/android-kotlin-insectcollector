package androidkotlin.fanjavaid.com.insectcollector.insect_details

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidkotlin.fanjavaid.com.insectcollector.R
import androidkotlin.fanjavaid.com.insectcollector.add_insects.AddInsectActivity
import androidkotlin.fanjavaid.com.insectcollector.data.Insect
import androidkotlin.fanjavaid.com.insectcollector.data.InsectContract
import androidkotlin.fanjavaid.com.insectcollector.data.InsectContract.InsectEntry
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_insect_details.*
import java.io.File

class InsectDetailsActivity : AppCompatActivity() {

    var insect: Insect? = null

    companion object {
        val EXTRA_INSECT = "extra_insect"
        val REQUEST_CODE_INSECT_EDIT = 1992
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insect_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.text_title_details)

        insect = intent.extras?.get(EXTRA_INSECT) as Insect

        setupInsectData()

        insect_detail_edit.setOnClickListener {
            val editIntent = Intent(this, AddInsectActivity::class.java)
            editIntent.putExtra(AddInsectActivity.ARG_EXTRA_INSECT_DATA, insect)

            startActivityForResult(editIntent, REQUEST_CODE_INSECT_EDIT)
        }
    }

    fun setupInsectData() {
        insect_detail_name.text = insect?.name
        insect_detail_information.text = insect?.info

        Glide.with(this)
                .load(insect?.picture)
                .into(insect_detail_picture)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_favorite -> {
                val contentValues: ContentValues = ContentValues()

                insect?.let {
                    val returnValue = contentResolver.update(
                            Uri.parse("${InsectContract.InsectEntry.CONTENT_URI}/${insect!!.id}"),
                            contentValues.getInsect(insect!!),
                            null,
                            null
                    )

                    if (returnValue != -1) {
                        Toast.makeText(this, getString(R.string.text_add_favorite_success), Toast.LENGTH_SHORT).show()
                    }
                }

            }

            R.id.action_delete -> {
                AlertDialog.Builder(this)
                        .setTitle(getString(R.string.text_discard_confirm_title))
                        .setMessage(getString(R.string.text_discard_confirm_message, insect?.name))
                        .setPositiveButton(getString(R.string.text_discard_confirm_button_positive), { _, _ ->
                            insect?.let {
                                val returnValue = contentResolver.delete(
                                        InsectEntry.CONTENT_URI.buildUpon().appendPath("${insect!!.id}").build(),
                                        null,
                                        null
                                )

                                // Delete file
                                if (returnValue != -1) {
                                    File(insect!!.picture).delete()
                                    finish()
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.text_discard_confirm_button_negative), { _, _ ->

                        })
                        .create()
                        .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when {
            resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_INSECT_EDIT -> {
                insect = data?.extras?.getParcelable(EXTRA_INSECT)
                setupInsectData()
            }
        }
    }

    fun ContentValues.getInsect(insectData: Insect) : ContentValues {
        this.put(InsectEntry._ID, insectData.id)
        this.put(InsectEntry.COLUMN_NAME, insectData.name)
        this.put(InsectEntry.COLUMN_DESCRIPTION, insectData.info)
        this.put(InsectEntry.COLUMN_PHOTO, insectData.picture)
        this.put(InsectEntry.COLUMN_IS_FAVORITE, 1)

        return this
    }
}
