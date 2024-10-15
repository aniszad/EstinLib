package com.az.elib.di

import com.az.elib.data.repository.RepositoryAuth
import com.az.elib.data.repository.RepositoryFeed
import com.az.elib.domain.usecases.postfeed.AddReactionUserCase
import com.az.elib.domain.usecases.postfeed.GetPostsBatchUseCase
import com.az.elib.domain.usecases.postfeed.RemoveReactionUserCase
import com.az.elib.util.ModelMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object ModuleFeed {
    @Provides
    fun providesModelMapper() : ModelMapper{
        return ModelMapper()
    }
    @Provides
    fun providesGetPostsBatchUserCase(repositoryFeed: RepositoryFeed) : GetPostsBatchUseCase {
        return GetPostsBatchUseCase(repositoryFeed)
    }
    @Provides
    fun providesAddReactionUserCase(repositoryFeed: RepositoryFeed, repositoryAuth: RepositoryAuth) : AddReactionUserCase {
        return AddReactionUserCase(repositoryFeed, repositoryAuth)
    }

    @Provides
    fun providesRemoveReactionUserCase(repositoryFeed: RepositoryFeed, repositoryAuth: RepositoryAuth) : RemoveReactionUserCase {
        return RemoveReactionUserCase(repositoryFeed, repositoryAuth)
    }
}