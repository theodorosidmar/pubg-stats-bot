package com.github.theodorosidmar.pubg

interface PubgApi {
    suspend fun getLifetimeStats(player: String, gameMode: GameMode): Stats?
}
