package com.reactivemobile.websocketdemo

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