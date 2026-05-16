<p style="text-align: center">
  <img src="assets/banner.png" alt="PUBG Stats Bot" />
</p>

<p style="text-align: center">
  A Discord bot that delivers PUBG player statistics directly to your server.
</p>

<p style="text-align: center">
  <img src="https://img.shields.io/badge/Kotlin-2.4.0-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/JVM-26-orange" alt="JVM 26" />
  <img src="https://img.shields.io/badge/Discord-Kord_0.18-5865F2?logo=discord&logoColor=white" alt="Kord" />
  <img src="https://img.shields.io/badge/PUBG_API-pubgkt-F2A900" alt="pubgkt" />
  <img src="https://github.com/theodorosidmar/pubg-stats-bot/actions/workflows/ci.yml/badge.svg" alt="CI" />
  <img src="https://github.com/theodorosidmar/pubg-stats-bot/actions/workflows/detekt.yml/badge.svg" alt="Detekt" />
</p>

---

# PUBG Stats Bot
## Features

- Slash commands with localized descriptions (English & Portuguese)
- PUBG API integration with rate limiting and automatic retries
- Virtual threads for command processing

## Requirements

- JDK 26+
- A [Discord bot token](https://discord.com/developers/applications)
- A [PUBG API key](https://developer.pubg.com/)

## Running

```sh
./gradlew jar && java -jar build/libs/*.jar --discord-token=<token> --pubg-api-key=<key>
```

Configuration can also be provided via system properties or environment variables:

| CLI argument        | System property     | Environment variable |
|---------------------|---------------------|----------------------|
| `--discord-token`   | `-Ddiscord.token`   | `DISCORD_TOKEN`      |
| `--pubg-api-key`    | `-Dpubg.api.key`    | `PUBG_API_KEY`       |
| `--gateway-workers` | `-Dgateway.workers` | `GATEWAY_WORKERS`    |

Use `--debug` to enable debug logging for all libraries, or `-Dorg.slf4j.simpleLogger.log.dev.pubgstats=debug` to debug only the bot.

## Tech stack

| Layer                  | Library                                            |
|------------------------|----------------------------------------------------|
| Discord gateway & REST | [Kord](https://github.com/kordlib/kord)            |
| PUBG API client        | [pubgkt](https://github.com/theodorosidmar/pubgkt) |
| Logging                | SLF4J + slf4j-simple                               |
| Static analysis        | detekt + ktlint                                    |
| Coverage               | Kover                                              |

## License

This project is for educational purposes.
