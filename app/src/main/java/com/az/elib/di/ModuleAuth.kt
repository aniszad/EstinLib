package com.az.elib.di

import android.content.Context
import com.az.elib.data.datasource.FirebaseAuth
import com.az.elib.data.datasource.FirebaseFirestoreAuth
import com.az.elib.data.local.MySharedPreferences
import com.az.elib.data.repository.RepositoryAuth
import com.az.elib.data.repository.RepositoryFirestoreAuth
import com.az.elib.domain.usecases.auth.SignUpUseCase
import com.az.elib.util.InputChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ModuleAuth {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth()
    }

    @Provides
    fun provideRepositoryAuth(firebaseAuth: FirebaseAuth): RepositoryAuth {
        return RepositoryAuth(firebaseAuth)
    }

    @Provides
    fun provideFirebaseFirestoreAuth(): FirebaseFirestoreAuth {
        return FirebaseFirestoreAuth()
    }

    @Provides
    fun provideRepositoryFirestore(firebaseFirestore: FirebaseFirestoreAuth): RepositoryFirestoreAuth {
        return RepositoryFirestoreAuth(firebaseFirestore)
    }

    @Provides
    fun provideEditeTextManager(): InputChecker {
        return InputChecker()
    }

    @Provides
    fun provideMySharedPreferences(context: Context): MySharedPreferences {
        return MySharedPreferences(context)
    }

    @Provides
    fun provideSignUpUseCase(
        repositoryAuth: RepositoryAuth,
        repositoryFirestore: RepositoryFirestoreAuth,
        editTextManager: InputChecker
    ): SignUpUseCase {
        return SignUpUseCase(repositoryAuth, repositoryFirestore, editTextManager)
    }


}