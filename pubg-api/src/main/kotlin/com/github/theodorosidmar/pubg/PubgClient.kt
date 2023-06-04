package com.github.theodorosidmar.pubg

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.serialization
import kotlinx.serialization.json.Json

class PubgClient(private val pubgApiKey: String) {
    private companion object {
        const val PUBG_API_PATH = "https://api.pubg.com/shards/steam"
        const val PLAYERS_PATH = "/players"
        const val FILTER_PLAYER_NAMES = "filter[playerNames]"
        const val FILTER_PLAYER_IDS = "filter[playerIds]"
    }
    private val pubgClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            serialization(
                ContentType.Application.Json,
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                    isLenient = true
                    allowSpecialFloatingPointValues = true
                    allowStructuredMapKeys = true
                    prettyPrint = false
                    useArrayPolymorphism = false
                },
            )
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
        defaultRequest {
            bearerAuth(pubgApiKey)
            contentType(ContentType("application", "vnd.api+json"))
        }
    }

    suspend fun getLifetimeStats(player: String, gameMode: GameMode): Stats? {
        val accountId: String = pubgClient.get("$PUBG_API_PATH$PLAYERS_PATH") {
            url {
                parameters.append(FILTER_PLAYER_NAMES, player)
            }
        }.body<PlayerResponse>().data.first().id
        val lifetime: LifetimeResponse = pubgClient.get("$PUBG_API_PATH/seasons/lifetime/gameMode/${gameMode.id}/players") {
            url {
                parameters.append(FILTER_PLAYER_IDS, accountId)
            }
        }.body()
        return lifetime.data.first().attributes.gameModeStats[gameMode.id]
    }
}
