from os import getenv
from discord.ext import commands
from pubg_python import PUBG, Shard
from lifetime import Lifetime

pubg_api = PUBG(getenv('PUBG_API_KEY'), Shard.STEAM)

DISCORD_TOKEN = getenv('DISCORD_TOKEN')
COMMAND_PREFIX = getenv('DISCORD_COMMAND_PREFIX')

bot = commands.Bot(command_prefix=COMMAND_PREFIX)
bot.add_cog(Lifetime(bot, pubg_api))

# TODO: Need this?
@bot.event
async def on_command_error(ctx, error):
    if isinstance(error, commands.errors.CheckFailure):
        await ctx.send('You do not have the correct role for this command.')

bot.run(DISCORD_TOKEN)
