package com.example.animals.di

import com.example.animals.model.AnimalApiService
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {
    fun inject(service: AnimalApiService)
}