from os import getenv
import pubg
from discord.ext import commands

TOKEN = getenv('DISCORD_TOKEN')
COMMAND_PREFIX = getenv('DISCORD_COMMAND_PREFIX')

bot = commands.Bot(command_prefix=COMMAND_PREFIX)


@bot.command(name='stats', help='Responds with players stats')
async def stats(ctx, *players):
    async with ctx.typing():
        value = pubg.players(players)[0:2000]
    await ctx.send(value)


@bot.event
async def on_command_error(ctx, error):
    if isinstance(error, commands.errors.CheckFailure):
        await ctx.send('You do not have the correct role for this command.')

bot.run(TOKEN)
