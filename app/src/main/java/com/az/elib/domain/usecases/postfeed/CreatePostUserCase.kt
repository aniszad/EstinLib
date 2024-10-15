package com.az.elib.domain.usecases.postfeed

import android.net.Uri
import android.util.Log
import com.az.elib.data.local.MySharedPreferences
import com.az.elib.data.repository.RepositoryCreatePost
import com.az.elib.domain.models.Post
import com.google.firebase.Timestamp
import javax.inject.Inject

class CreatePostUserCase @Inject constructor(
    private val mySharedPref: MySharedPreferences,
    private val repositoryCreatePost: RepositoryCreatePost
    ) {

    suspend fun invoke(etPost: String, tag: String?, channel:String? ,attachList: HashMap<String, Uri>?) : Result<Post> {
        val postImageAttachments = mutableListOf<String>()
        return try {
            val postContentIsEmpty = etPost.isBlank() && attachList.isNullOrEmpty()
            if (postContentIsEmpty) throw Exception("Post content is empty")
            val user = mySharedPref.getUser()
            val firebaseDocRef = repositoryCreatePost.getFirebaseDocRef()
            if (firebaseDocRef.isFailure) throw Exception("Failed to get firebase doc ref")
            val postId = firebaseDocRef.getOrThrow()
            val post = Post(
                id=postId,
                ownerId= user.id,
                ownerFirstName=user.firstName,
                ownerLastName=user.lastName,
                ownerEmail=user.email,
                ownerYear=user.year,
                ownerImage=user.image,
                postContent=etPost,
                postImageAttachments = emptyList(),
                tag = tag,
                channel= channel ?: "All",
                postReactCount = 0,
                postLikesIds = emptyList(),
                timestamp= Timestamp.now()
            )
            if (attachList != null) {
                for (attach in attachList) {
                    Log.e("upload check", "$attach")
                    val attachDownloadUrl = repositoryCreatePost.uploadAttach(attach, post.ownerId, postId)
                    postImageAttachments.add(attachDownloadUrl.getOrThrow())
                }
            }
            post.postImageAttachments = postImageAttachments
            val result = repositoryCreatePost.createPost(post)
            result
        } catch (e: Exception) {
            Log.e("CreatePostUserCase", "invoke: ${e.stackTraceToString()}")
            if (postImageAttachments.isNotEmpty()){
                for (postImageAttachment in postImageAttachments){
                    repositoryCreatePost.deleteAttach(postImageAttachment)
                }
            }
            Result.failure(e)
        }
    }
}