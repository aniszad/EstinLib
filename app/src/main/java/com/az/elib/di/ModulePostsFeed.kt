package com.az.elib.di

import com.az.elib.data.datasource.FirebaseFirestoreFeed
import com.az.elib.data.repository.RepositoryFeed
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object ModulePostsFeed {

    @Provides
    fun provideFirebaseFirestoreFeed(): FirebaseFirestoreFeed {
        return FirebaseFirestoreFeed()
    }

    @Provides
    fun provideRepositoryFeed(firestoreFeed: FirebaseFirestoreFeed): RepositoryFeed {
        return RepositoryFeed(firestoreFeed)
    }
}