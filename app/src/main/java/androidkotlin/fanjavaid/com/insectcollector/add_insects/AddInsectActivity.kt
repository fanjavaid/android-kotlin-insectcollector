package androidkotlin.fanjavaid.com.insectcollector.add_insects

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View.*
import androidkotlin.fanjavaid.com.insectcollector.R
import androidkotlin.fanjavaid.com.insectcollector.data.Insect
import androidkotlin.fanjavaid.com.insectcollector.data.InsectContract
import androidkotlin.fanjavaid.com.insectcollector.extension.InsectExtension.Companion.asInsectData
import androidkotlin.fanjavaid.com.insectcollector.insect_details.InsectDetailsActivity
import androidkotlin.fanjavaid.com.insectcollector.util.Constants
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_add_insect.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AddInsectActivity : AppCompatActivity() {
    var maxChars: Int = 0
    var remainingCharCount = 0
    var insect: Insect? = null

    private var returnedImage: File? = null

    companion object {
        val TAG: String = AddInsectActivity::class.java.simpleName
        val REQUEST_IMAGE_CAPTURE = 101
        val MY_PERMISSIONS_REQUEST_CAMERA = 111
        val IMAGE_NAME_DATE_PATTERN = "yyyyMMMdd_HHmmss"
        val IMAGE_PREFIX_NAME = "INSECT"
        val IMAGE_SUFFIX_EXT = ".jpg"

        val ARG_EXTRA_INSECT_DATA = "arg_extra_insect_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_insect)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.text_title_add_new)

        maxChars = resources.getInteger(R.integer.max_char)

        intent?.extras?.let {
            populateFormForUpdate(intent.extras.containsKey(ARG_EXTRA_INSECT_DATA),
                    intent.extras.getParcelable(ARG_EXTRA_INSECT_DATA))
            supportActionBar?.title = getString(R.string.text_title_update_existing)
        }

        image_add_insect_capture_action.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                        MY_PERMISSIONS_REQUEST_CAMERA)
            } else {
                dispatchTakePictureIntent()
            }

        }

        button_add_insect_remove_capture?.setOnClickListener {
            returnedImage?.delete()
            resetCapturedImage()
        }

        edittext_add_insect_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                input_layout_insect_name.isErrorEnabled = false
                input_layout_insect_name.error = null
            }

        })

        edittext_add_insect_info.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) edittext_add_insect_info.gravity = Gravity.TOP else {
                if (edittext_add_insect_info.text?.length == 0) {
                    edittext_add_insect_info.gravity = Gravity.BOTTOM
                }
            }
        }

        text_add_insect_remaining_char.text = getString(R.string.text_add_insect_remaining_char,
                0, maxChars)
        edittext_add_insect_info.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                remainingCharCount = p0?.length ?: 0
                text_add_insect_remaining_char.text = getString(R.string.text_add_insect_remaining_char,
                        remainingCharCount, maxChars)

                input_layout_insect_info.isErrorEnabled = false
                input_layout_insect_info.error = null
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA ->
                if (grantResults.isNotEmpty() && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = BitmapFactory.decodeFile(returnedImage?.absolutePath)
            image_add_insect_capture.setImageBitmap(imageBitmap)
            showCapturedImage()
        }
    }

    private fun showCapturedImage() {
        image_add_insect_capture.visibility = VISIBLE
        button_add_insect_remove_capture.visibility = VISIBLE
        image_add_insect_capture_action.visibility = GONE
    }

    private fun resetCapturedImage() {
        image_add_insect_capture.setImageBitmap(null)
        button_add_insect_remove_capture.visibility = GONE
        image_add_insect_capture_action.visibility = VISIBLE
    }

    private fun resetField() {
        edittext_add_insect_name.setText("")
        edittext_add_insect_name.requestFocus()

        edittext_add_insect_info.setText("")
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File = saveImageToLocalStorage()
            val photoUri: Uri = FileProvider.getUriForFile(this,
                    Constants.PHOTO_FILE_PROVIDER, photoFile)

            Log.d(TAG, photoUri.toString())

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun saveImageToLocalStorage(): File {
        // Create an image file name
        val timestamp = SimpleDateFormat(IMAGE_NAME_DATE_PATTERN, Locale.US).format(Date())
        val imageFileName = "${IMAGE_PREFIX_NAME}_${timestamp}_"

        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image: File = File.createTempFile(
                imageFileName,
                IMAGE_SUFFIX_EXT,
                storageDir
        )

        returnedImage = image

        return image
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_save_changes -> {
                actionSaveChanges()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun actionSaveChanges() {
        when {
            TextUtils.isEmpty(edittext_add_insect_name.text) -> {
                input_layout_insect_name.isErrorEnabled = true
                input_layout_insect_name.error = getString(R.string.text_add_insect_name_error)

            }
            TextUtils.isEmpty(edittext_add_insect_info.text) -> {
                input_layout_insect_info.isErrorEnabled = true
                input_layout_insect_info.error = getString(R.string.text_add_insect_info_error)

            }
            null == returnedImage -> Snackbar.make(scrollview, getString(R.string.text_add_insect_photo_error),
                    Snackbar.LENGTH_SHORT).show()
            else -> {
                var uri = InsectContract.InsectEntry.CONTENT_URI
                val contentValues = ContentValues()
                contentValues.put(InsectContract.InsectEntry.COLUMN_NAME,
                        edittext_add_insect_name.text.toString())
                contentValues.put(InsectContract.InsectEntry.COLUMN_DESCRIPTION,
                        edittext_add_insect_info.text.toString())
                contentValues.put(InsectContract.InsectEntry.COLUMN_PHOTO,
                        returnedImage?.absolutePath)

                if (insect != null) {
                    uri = uri.buildUpon().appendPath("${insect!!.id}").build()
                    contentResolver.update(uri, contentValues, null, null)

                    // Send updated data back to details activity
                    val updatedInsect: Cursor = contentResolver.query(
                            uri,
                            null,
                            null,
                            null,
                            null
                    )
                    updatedInsect.moveToFirst()

                    val intent = Intent()
                    intent.putExtra(InsectDetailsActivity.EXTRA_INSECT, updatedInsect.asInsectData())
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    contentResolver.insert(uri, contentValues)

                    Snackbar.make(scrollview, getString(R.string.text_add_insect_success),
                            Snackbar.LENGTH_SHORT).show()

                    resetCapturedImage()
                    resetField()
                }
            }
        }

    }

    // Update Functionality
    private fun populateFormForUpdate(dataExists: Boolean, insectData: Insect) {
        if (dataExists) {
            insect = insectData

            returnedImage = File(insectData.picture)
            edittext_add_insect_name.setText(insectData.name)
            edittext_add_insect_info.setText(insectData.info)
            Glide.with(this)
                    .load(insectData.picture)
                    .into(image_add_insect_capture)

            button_add_insect_remove_capture.visibility = VISIBLE
            image_add_insect_capture_action.visibility = GONE
        }
    }
}
