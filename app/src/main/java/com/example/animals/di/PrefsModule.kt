package com.example.animals.di

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.example.animals.utils.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import javax.inject.Singleton

const val CONTEXT_APP = "app context"
const val CONTEXT_ACTIVITY = "activity context"

@Module
open class PrefsModule {

    @Provides
    @Singleton
    @TypeOfContext(CONTEXT_APP)
    open fun provideSharedPreferences(app: Application): SharedPreferencesHelper {
        return SharedPreferencesHelper(app)
    }

    @Provides
    @Singleton
    @TypeOfContext(CONTEXT_ACTIVITY)
    fun provideActivitySharedPreferences(activity: AppCompatActivity): SharedPreferencesHelper {
        return SharedPreferencesHelper(activity)
    }
    @Qualifier
    annotation class TypeOfContext(val type: String);
}