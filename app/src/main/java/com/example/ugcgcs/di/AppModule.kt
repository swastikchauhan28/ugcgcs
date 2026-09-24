package com.example.ugcgcs.di

import com.example.ugcgcs.data.mavlink.MavlinkConnection
import com.example.ugcgcs.data.mock.MockMavlinkConnection
import com.example.ugcgcs.data.repository.MavlinkVehicleRepository
import com.example.ugcgcs.domain.repository.VehicleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds @Singleton
    abstract fun bindMavlinkConnection(implementation: MockMavlinkConnection): MavlinkConnection

    @Binds @Singleton
    abstract fun bindVehicleRepository(implementation: MavlinkVehicleRepository): VehicleRepository
}
