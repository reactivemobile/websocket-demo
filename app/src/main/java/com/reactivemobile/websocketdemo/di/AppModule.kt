package com.reactivemobile.websocketdemo.di

import com.reactivemobile.websocketdemo.data.Server
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(
    ActivityRetainedComponent::class
)
class AppModule() {

    @Provides
    fun provideServer(): Server = Server()
}