import os
from discord.ext import commands

from dotenv import load_dotenv
load_dotenv()

TOKEN = os.getenv('DISCORD_TOKEN')
COMMAND_PREFIX = os.getenv('DISCORD_COMMAND_PREFIX')
bot = commands.Bot(command_prefix=COMMAND_PREFIX)


@bot.command(name='ping', help='Responds with pong message')
async def pong(ctx):
    await ctx.send('pong')


@bot.command(name='stats', help='Responds with players stats')
async def stats(ctx, mode="squad", *players):
    if not players:
        players = ["TestemunhaDeJah", "CapotaBlaser"]
    players = ", ".join(players)
    await ctx.send(f'`{mode} stats players {players}`')


@bot.event
async def on_command_error(ctx, error):
    if isinstance(error, commands.errors.CheckFailure):
        await ctx.send('You do not have the correct role for this command.')

bot.run(TOKEN)
