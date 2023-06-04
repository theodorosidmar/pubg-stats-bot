package com.github.theodorosidmar.pubg


internal const val PUBG_API_PATH = "https://api.pubg.com/shards"
internal const val PUBG_STEAM_SHARD = "steam"
internal const val PUBG_STEAM_PATH = "$PUBG_API_PATH/$PUBG_STEAM_SHARD"
internal const val PLAYERS_PATH = "/players"
internal const val FILTER_PLAYER_NAMES = "filter[playerNames]"
internal const val FILTER_PLAYER_IDS = "filter[playerIds]"

interface PubgApi {
    suspend fun getLifetimeStats(player: String, gameMode: GameMode): Stats?
}
