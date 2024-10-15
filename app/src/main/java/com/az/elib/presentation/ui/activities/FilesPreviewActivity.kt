package com.az.elib.presentation.ui.activities


import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import com.az.androiddrivepreview.data.GoogleDriveFileManager
import com.az.androiddrivepreview.utils.Permissions
import com.az.elib.R
import com.az.elib.data.local.CredentialsProvider
import com.az.elib.data.local.MySharedPreferences
import com.az.elib.databinding.ActivityFilesPreviewBinding
import com.az.elib.util.Constants
import java.io.File


class FilesPreviewActivity : BaseActivity() {
    private lateinit var binding : ActivityFilesPreviewBinding
    private lateinit var gdm : GoogleDriveFileManager
    private val sharedPref : MySharedPreferences by lazy{ MySharedPreferences(this@FilesPreviewActivity) }
    private lateinit var driveFolderId: String
    private var rootFolderTitle : String? = null
    private val activityDownloadFolderAccessResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            sharedPref.isDownloadFolderAccessGranted = true
            Log.e("DOWNLOAD_DRIVE", "$uri")
            gdm.setDownloadPath(uri)
        } else {
            Log.e("FolderAccess", "Permission denied, cannot create folder. Downloads are not possible.")
            sharedPref.isDownloadFolderAccessGranted = false
        }
    }




    private val requestWritePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                createDownloadFolder()
            } else {
                Toast.makeText(
                    this@FilesPreviewActivity,
                    "Permission denied, cannot create folder",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFilesPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSystemBarsColors(ContextCompat.getColor(this@FilesPreviewActivity, R.color.black))
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.main.id)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setStatusBarLight(false)
        getIntentData()

        setupDownloadFolder()
        // Handle back press
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                gdm.navigateBack { this@FilesPreviewActivity.finish() }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)


        /**
         * setupDownloadFolder
         *
         * This function is used to setup the download folder for the app.
         * including creating the folder and granting permission to it, and then
         * calling gdm.setDownloadPath() to set the download path.
         *
         * not implementing means using the default behavior which is downloading into :
         * "context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path"
         *
         */
        //setupDownloadFolder()

    }

    private fun getIntentData() {
        if(intent.extras != null){
            driveFolderId = intent.getStringExtra(Constants.DRIVE_FOLDER_ID) ?: ""
            rootFolderTitle = intent.getStringExtra(Constants.DRIVE_FOLDER_NAME)
            if (driveFolderId.isNotBlank()){
                setDrivePreview()
            }
        }
    }

    private fun setDrivePreview() {
        gdm = GoogleDriveFileManager(
            this@FilesPreviewActivity,
            lifecycleScope, // lifecycle scope for launching coroutines
            Permissions.ADMIN,
            CredentialsProvider(this@FilesPreviewActivity)
        )

        // Set the recycler view, toolbar, root file id, root folder name, and the file picker listener
        gdm.setRecyclerView(binding.recyclerView) // set recycler view to display files
            .setActionBar(binding.toolbar) // set toolbar to display file name, actions, path
            .setRootFileId(rootFileId = driveFolderId) // the id of the drive file to be displayed
            .setRootFolderName(rootFolderTitle ?: "Files Bank") // the root file name
            .activateNavigationPath(false) // set to true to display the path of the current directory
            .setFilePathCopyable(true) // set to true to allow the user to copy the path of the current directory
            .setThemeMode(true)
            .initialize() // initialize the GoogleDriveFileManager

    }

    private fun setupDownloadFolder() {
        val appFolderName = getString(R.string.app_name)
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val folderPath = File(downloadsDir, appFolderName)

        if (!folderPath.exists()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                requestFolderCreationPermission()
            }else{
                createDownloadFolder()
            }
        }else{
            if(!sharedPref.isDownloadFolderAccessGranted){
                openFolderForAccess(folderPath.toUri())
            }
        }
    }
    private fun openFolderForAccess(folderUri:Uri) {
        try {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, getInitialUri())
            }
            activityDownloadFolderAccessResultLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("FolderAccess", "No activity found to handle the intent", e)
        }
    }

    inline fun <reified T : Parcelable> Intent.getParcelableExtraExt(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelableExtra(key) as? T
        }
    }
    private fun getInitialUri(): Uri {
        val appFolderName = getString(R.string.app_name)
        val downloadsFolder = "Downloads"
        val initialUri = DocumentsContract.buildDocumentUri(
            "com.android.externalstorage.documents",
            "primary:$downloadsFolder"
        )
        return try {
            DocumentsContract.buildDocumentUri(
                "com.android.externalstorage.documents",
                "primary:$downloadsFolder/$appFolderName"
            )
        } catch (e: Exception) {
            // If creating the specific URI fails, fall back to the Downloads folder
            initialUri
        }
    }

    private fun createDownloadFolder() {
        val appFolderName = getString(R.string.app_name)
        // Folder doesn't exist, create it
        val relativePath = Environment.DIRECTORY_DOWNLOADS + "/" + appFolderName
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, appFolderName)
            put(MediaStore.Downloads.RELATIVE_PATH, relativePath)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        try {
            val folderUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            if (folderUri!=null){
                openFolderForAccess(folderUri)
            }
        } catch (e: Exception) {
            Log.e("FolderCreation", "Failed to create folder: ${e.message}")
        }
    }
    private fun requestFolderCreationPermission() {
        // Check if permissions have been granted
        if (!hasPermissions(this@FilesPreviewActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            // Request permissions
            requestWritePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            createDownloadFolder()
        }
    }
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
    private fun isFolderNameCorrect(uri: Uri): Boolean {
        val documentFile = DocumentFile.fromTreeUri(this@FilesPreviewActivity, uri)
        val folderName = documentFile?.name
        return folderName == getString(R.string.app_name)
    }


}