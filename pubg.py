from os import getenv
import requests

api_key = getenv('PUBG_TOKEN')
players_url = 'https://api.pubg.com/shards/steam/players'


def players(players):
    headers = {'Authorization': f'Bearer {api_key}',
               'Accept': 'application/vnd.api+json'}
    params = {'filter[playerNames]': ", ".join(players)}
    response = requests.get(players_url, headers=headers, params=params)
    return response.text
