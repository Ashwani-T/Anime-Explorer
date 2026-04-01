package com.example.animeexplorer.di

import com.example.animeexplorer.core.data.connectivity.ConnectivityObserver
import com.example.animeexplorer.core.data.connectivity.NetworkConnectivityObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule{

    @Binds
    @Singleton
    abstract fun bindConnectivityObserver(
        impl: NetworkConnectivityObserver
    ): ConnectivityObserver

}
