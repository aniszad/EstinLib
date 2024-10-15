package com.az.elib.di


import com.az.elib.data.datasource.FirebaseFirestoreUser
import com.az.elib.data.repository.RepositoryUser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ModuleSettings {

    @Provides
    fun provideFirebaseFirestoreUser() : FirebaseFirestoreUser {
        return FirebaseFirestoreUser()
    }

    @Provides
    fun provideRepositoryUser(firebaseFirestoreUser: FirebaseFirestoreUser) : RepositoryUser {
        return RepositoryUser(firebaseFirestoreUser)
    }
}