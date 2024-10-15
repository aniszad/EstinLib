package com.az.elib.data.local

import android.content.Context
import android.content.SharedPreferences
import com.az.elib.domain.models.UserLocalData


class MySharedPreferences(context: Context) {

    companion object {
        const val NOTIFICATIONS = "notifications"
        const val KEY_USER_ID = "userId"
        const val KEY_USER_EMAIL = "userEmail"
        const val KEY_USER_FIRST_NAME = "userFirstName"
        const val KEY_USER_LAST_NAME = "userLastName"
        const val KEY_USER_IMAGE = "userImage"
        const val KEY_USER_DETAILS = "userDetails"

        private const val PREF_NAME = "download_prefs"
        private const val KEY_DOWNLOAD_FOLDER_ACCESS_GRANTED = "download_folder_access_granted"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private val notifications = context.getSharedPreferences(NOTIFICATIONS, Context.MODE_PRIVATE)
    private val userId = context.getSharedPreferences(KEY_USER_ID, Context.MODE_PRIVATE)
    private val userEmail = context.getSharedPreferences(KEY_USER_EMAIL, Context.MODE_PRIVATE)
    private val userFirstName = context.getSharedPreferences(KEY_USER_FIRST_NAME, Context.MODE_PRIVATE)
    private val userLastName = context.getSharedPreferences(KEY_USER_LAST_NAME, Context.MODE_PRIVATE)
    private val userImage = context.getSharedPreferences(KEY_USER_IMAGE, Context.MODE_PRIVATE)
    private val userDetails = context.getSharedPreferences(KEY_USER_DETAILS, Context.MODE_PRIVATE)

    private val notificationsEditor = notifications.edit()
    private val userIdEditor = userId.edit()
    private val userEmailEditor = userEmail.edit()
    private val userFirstNameEditor = userFirstName.edit()
    private val userLastNameEditor = userLastName.edit()
    private val userImageEditor = userImage.edit()
    private val userDetailsEditor = userDetails.edit()

    fun saveNotificationAllowed(notificationsAllowed: Boolean) {
        notificationsEditor.putBoolean(NOTIFICATIONS, notificationsAllowed)
        notificationsEditor.apply()
    }
    fun getNotificationAllowed(): Boolean {
        return notifications.getBoolean(NOTIFICATIONS, true)
    }

    private fun saveUserId(userId: String) {
        userIdEditor.putString(KEY_USER_ID, userId)
        userIdEditor.apply()
    }
    fun getUserId(): String? {
        return userId.getString(KEY_USER_ID, "")
    }
    private fun saveUserEmail(userEmail: String) {
        userEmailEditor.putString(KEY_USER_EMAIL, userEmail)
        userEmailEditor.apply()
    }
    fun getUserEmail(): String? {
        return userEmail.getString(KEY_USER_EMAIL, "")
    }
    private fun saveUserFirstName(userFirstName: String) {
        userFirstNameEditor.putString(KEY_USER_FIRST_NAME, userFirstName)
        userFirstNameEditor.apply()
    }
    fun getUserFirstName(): String? {
        return userFirstName.getString(KEY_USER_FIRST_NAME, "")
    }
    fun getFullName() : String? {
        val firstName = getUserFirstName()
        val lastName = getUserLastName()
        return if (firstName != null && lastName != null) "$lastName $firstName" else null
    }
    private fun saveUserLastName(userLastName: String) {
        userLastNameEditor.putString(KEY_USER_LAST_NAME, userLastName)
        userLastNameEditor.apply()
    }
    fun getUserLastName(): String? {
        return userLastName.getString(KEY_USER_LAST_NAME, "")
    }
    private fun saveUserImage(userImage: String?) {
        userImageEditor.putString(KEY_USER_IMAGE, userImage)
        userImageEditor.apply()
    }
    fun getUserImage(): String? {
        return userImage.getString(KEY_USER_IMAGE, "")
    }
    private fun saveUserDetails(userDetails: String?) {
        userDetailsEditor.putString(KEY_USER_DETAILS, userDetails)
        userDetailsEditor.apply()
    }
    fun getUserDetails(): String? {
        return userDetails.getString(KEY_USER_DETAILS, "")
    }
    fun clearAll() {
        userIdEditor.clear().apply()
        userEmailEditor.clear().apply()
        userFirstNameEditor.clear().apply()
        userLastNameEditor.clear().apply()
        userImageEditor.clear().apply()
        userDetailsEditor.clear().apply()
        notificationsEditor.clear().apply()
    }

    fun saveUserLocalData(user: UserLocalData) {
            saveUserId(user.id)
            saveUserEmail(user.email)
            saveUserFirstName(user.firstName)
            saveUserLastName(user.lastName)
            saveUserImage(user.image)
            saveUserDetails(user.year)
    }

    fun getUser(): UserLocalData {
        return UserLocalData(
            id = getUserId() ?: "",
            email = getUserEmail() ?: "",
            firstName = getUserFirstName() ?: "",
            lastName = getUserLastName() ?: "",
            image = getUserImage(),
            year = getUserDetails()
        )
    }

    fun getChannel(): String {
        return getUserDetails() ?: "All"
    }

    var isDownloadFolderAccessGranted: Boolean
        get() = sharedPreferences.getBoolean(KEY_DOWNLOAD_FOLDER_ACCESS_GRANTED, false)
        set(value) {
            sharedPreferences.edit().putBoolean(KEY_DOWNLOAD_FOLDER_ACCESS_GRANTED, value).apply()
        }
}