package com.example.gardenbotapp.data.domain

interface MainRepository : GardenBotRepository {
    suspend fun checkToken(token: String):Boolean
}