from discord.ext import commands

class Lifetime(commands.Cog):
    def __init__(self, bot, pubg_api):
        self.bot = bot
        self.pubg_api = pubg_api

    @commands.command(name='squad', help='Responds with player Squad FPP stats')
    async def squad_stats(self, ctx, player_name):
        async with ctx.typing():
            player = self.pubg_api.players().filter(player_names=[player_name])[0]
            stats_data = self.pubg_api.seasons('lifetime', player_id=player.id).get()
            squad_data = stats_data.attributes['gameModeStats']['squad-fpp']
            alo = self.__formatter(squad_data)
        await ctx.send(alo)

    @commands.command(name='duo', help='Responds with player Duo FPP stats')
    async def duo_stats(self, ctx, player_name):
        async with ctx.typing():
            player = self.pubg_api.players().filter(player_names=[player_name])[0]
            stats_data = self.pubg_api.seasons('lifetime', player_id=player.id).get()
            duo_data = stats_data.attributes['gameModeStats']['duo-fpp']
        await ctx.send(self.__formatter(duo_data))

    @commands.command(name='solo', help='Responds with player Solo FPP stats')
    async def solo_stats(self, ctx, player_name):
        async with ctx.typing():
            player = self.pubg_api.players().filter(player_names=[player_name])[0]
            stats_data = self.pubg_api.seasons('lifetime', player_id=player.id).get()
            solo_data = stats_data.attributes['gameModeStats']['solo-fpp']
        await ctx.send(self.__formatter(solo_data))

    @commands.command(name='squad-tpp', help='Responds with player Squad TPP stats')
    async def squad_tpp_stats(self, ctx, player_name):
        async with ctx.typing():
            player = self.pubg_api.players().filter(player_names=[player_name])[0]
            stats_data = self.pubg_api.seasons('lifetime', player_id=player.id).get()
            squad_data = stats_data.attributes['gameModeStats']['squad']
        await ctx.send(self.__formatter(squad_data))

    @commands.command(name='duo-tpp', help='Responds with player Duo TPP stats')
    async def duo_tpp_stats(self, ctx, player_name):
        async with ctx.typing():
            player = self.pubg_api.players().filter(player_names=[player_name])[0]
            stats_data = self.pubg_api.seasons('lifetime', player_id=player.id).get()
            duo_data = stats_data.attributes['gameModeStats']['duo']
        await ctx.send(self.__formatter(duo_data))

    @commands.command(name='solo-tpp', help='Responds with player Solo TPP stats')
    async def solo_tpp_stats(self, ctx, player_name):
        async with ctx.typing():
            player = self.pubg_api.players().filter(player_names=[player_name])[0]
            stats_data = self.pubg_api.seasons('lifetime', player_id=player.id).get()
            solo_data = stats_data.attributes['gameModeStats']['solo']
        await ctx.send(self.__formatter(solo_data))

    def __formatter(self, data):
        return '''Seus stats:
        Armas looteadas: {weaponsAcquired}
        Assistencias: {assists}
        Boosts: {boosts}
        Dano causado: {damageDealt}
        Distancia dirigida: {rideDistance}
        Distancia nadada: {swimDistance}
        Distancia percoridda: {walkDistance}
        Fogo amigo: {teamKills}
        Headshot kills: {headshotKills}
        Heals: {heals}
        Kills: {kills}
        Kills em uma unica partida: {roundMostKills}
        Kill mais longe: {longestKill}
        Kills por atropelamento: {roadKills}
        Kill streak: {maxKillStreaks}
        Knocks: {dBNOs}
        Loses: {losses}
        Partidas jogadas: {roundsPlayed}
        Revives: {revives}
        Suicidios: {suicides}
        Tempo sobrevivido: {timeSurvived}
        Tempo sobrevivido (recorde): {longestTimeSurvived}
        Top 10: {top10s}
        Veiculos destruidos: {vehicleDestroys}
        '''.format(**data)
