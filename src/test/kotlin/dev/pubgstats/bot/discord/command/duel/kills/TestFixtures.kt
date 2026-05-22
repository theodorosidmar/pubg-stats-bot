package dev.pubgstats.bot.discord.command.duel.kills

import dev.pubgkt.GameMode
import dev.pubgkt.stats.GameModeStats
import dev.pubgkt.stats.lifetime.LifetimeGameModeStats

@Suppress("LongParameterList")
fun lifetimeGameModeStats(
    playerId: String = "test-player",
    gameMode: GameMode = GameMode.SQUAD_FPP,
    damageDealt: Double = 0.0,
    headshotKills: Int = 0,
    kills: Int = 0,
    longestKill: Double = 0.0,
    roadKills: Int = 0,
    roundMostKills: Int = 0,
): LifetimeGameModeStats = LifetimeGameModeStats(
    playerId = playerId,
    bestRankPoint = 0.0,
    gameMode = gameMode,
    stats = GameModeStats(
        assists = 0,
        boosts = 0,
        dbnos = 0,
        dailyKills = 0,
        dailyWins = 0,
        damageDealt = damageDealt,
        days = 0,
        headshotKills = headshotKills,
        heals = 0,
        kills = kills,
        longestKill = longestKill,
        longestTimeSurvived = 0.0,
        losses = 0,
        maxKillStreaks = 0,
        mostSurvivalTime = 0.0,
        revives = 0,
        rideDistance = 0.0,
        roadKills = roadKills,
        roundMostKills = roundMostKills,
        roundsPlayed = 0,
        suicides = 0,
        swimDistance = 0.0,
        teamKills = 0,
        timeSurvived = 0.0,
        top10s = 0,
        vehicleDestroys = 0,
        walkDistance = 0.0,
        weaponsAcquired = 0,
        weeklyKills = 0,
        weeklyWins = 0,
        wins = 0,
    ),
)
