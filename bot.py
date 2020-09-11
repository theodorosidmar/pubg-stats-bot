from os import getenv
from discord.ext import commands
from pubg_python import PUBG, Shard

pubg_api = PUBG(getenv('PUBG_API_KEY'), Shard.STEAM)

DISCORD_TOKEN = getenv('DISCORD_TOKEN')
COMMAND_PREFIX = getenv('DISCORD_COMMAND_PREFIX')

bot = commands.Bot(command_prefix=COMMAND_PREFIX)


@bot.command(name='stats', help='Responds with players stats')
async def stats(ctx, game_mode, player_name):
    async with ctx.typing():
        player = pubg_api.players().filter(player_names=[player_name])[0]
        stats = pubg_api.seasons('lifetime' , player_id=player.id).get()
    await ctx.send(stats.attributes['gameModeStats'][game_mode])

@bot.event
async def on_command_error(ctx, error):
    if isinstance(error, commands.errors.CheckFailure):
        await ctx.send('You do not have the correct role for this command.')

bot.run(DISCORD_TOKEN)
