package com.example.hds_tesisapp.di

import com.example.hds_tesisapp.data.repository.PlayerRepository
import com.example.hds_tesisapp.data.repository.PlayerRepositoryImpl
import com.example.hds_tesisapp.data.repository.ProgressRepository
import com.example.hds_tesisapp.data.repository.ProgressRepositoryImpl
import com.example.hds_tesisapp.data.repository.SessionRepository
import com.example.hds_tesisapp.data.repository.SessionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(impl: PlayerRepositoryImpl): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository
}
