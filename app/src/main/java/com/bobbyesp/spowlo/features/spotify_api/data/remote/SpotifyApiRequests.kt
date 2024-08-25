package com.bobbyesp.spowlo.features.spotify_api.data.remote

import android.util.Log
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.Album
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.AudioFeatures
import com.adamratzman.spotify.models.PagingObject
import com.adamratzman.spotify.models.Playlist
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.SpotifyPublicUser
import com.adamratzman.spotify.models.SpotifySearchResult
import com.adamratzman.spotify.models.Token
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.spotifyAppApi
import com.adamratzman.spotify.utils.Market
import com.bobbyesp.spowlo.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpotifyApiRequests {

    private val clientId = BuildConfig.CLIENT_ID ?: System.getenv("CLIENT_ID")
    private val clientSecret = BuildConfig.CLIENT_SECRET ?: System.getenv("CLIENT_SECRET")
    private var api: SpotifyAppApi? = null
    private var token: Token? = null

    @Provides
    @Singleton
    suspend fun provideSpotifyApi(): SpotifyAppApi {
        if (api == null) {
            buildApi()
        }
        return api!!
    }

    suspend fun buildApi() {
        Log.d(
            "SpotifyApiRequests",
            "Building API with client ID: $clientId and client secret: $clientSecret"
        )
        token = spotifyAppApi(clientId, clientSecret).build().token
        api = spotifyAppApi(clientId, clientSecret, token!!) {
            automaticRefresh = true
        }.build()
    }

    //Performs Spotify database query for queries related to user information.
    private suspend fun userSearch(userQuery: String): SpotifyPublicUser? {
        return provideSpotifyApi().users.getProfile(userQuery)
    }

    @Provides
    @Singleton
    suspend fun provideUserSearch(query: String): SpotifyPublicUser? {
        return userSearch("bobbyesp")
    }

    // Performs Spotify database query for queries related to track information.
    suspend fun searchAllTypes(searchQuery: String): SpotifySearchResult {
        kotlin.runCatching {
            provideSpotifyApi().search.searchAllTypes(
                searchQuery,
                limit = 50,
                offset = 0,
                market = Market.US
            )
        }.onFailure {
            Log.d("SpotifyApiRequests", "Error: ${it.message}")
            return SpotifySearchResult()
        }.onSuccess {
            return it
        }
        return SpotifySearchResult()
    }

    @Provides
    @Singleton
    suspend fun provideSearchAllTypes(query: String): SpotifySearchResult {
        return searchAllTypes(query)
    }

    private suspend fun searchTracks(searchQuery: String): List<Track> {
        kotlin.runCatching {
            provideSpotifyApi().search.searchTrack(searchQuery, limit = 50)
        }.onFailure {
            Log.d("SpotifyApiRequests", "Error: ${it.message}")
            return listOf()
        }.onSuccess {
            return it.items
        }
        return listOf()
    }

    @Provides
    @Singleton
    suspend fun provideSearchTracks(query: String): List<Track> {
        return searchTracks(query)
    }

    //search by id
    suspend fun getPlaylistById(id: String): Playlist? {
        kotlin.runCatching {
            provideSpotifyApi().playlists.getPlaylist(id)
        }.onFailure {
            Log.d("SpotifyApiRequests", "Error: ${it.message}")
            return null
        }.onSuccess {
            Log.d("SpotifyApiRequests", "Playlist: $it")
            return it
        }
        return null
    }

    @Provides
    @Singleton
    suspend fun provideGetPlaylistById(id: String): Playlist? {
        return getPlaylistById(id)
    }

    suspend fun getTrackById(id: String): Track? {
        kotlin.runCatching {
            provideSpotifyApi().tracks.getTrack(id)
        }.onFailure {
            Log.d("SpotifyApiRequests", "Error: ${it.message}")
            return null
        }.onSuccess {
            return it
        }
        return null
    }

    @Provides
    @Singleton
    suspend fun provideGetTrackById(id: String): Track? {
        return getTrackById(id)
    }

    private suspend fun getArtistById(id: String): Artist? {
        kotlin.runCatching {
            api!!.artists.getArtist(id)
        }.onFailure {
            Log.d("SpotifyApiRequests", "Error: ${it.message}")
            return null
        }.onSuccess {
            return it
        }
        return null
    }

    @Provides
    @Singleton
    suspend fun provideGetArtistById(id: String): Artist? {
        return getArtistById(id)
    }

    suspend fun getAlbumById(id: String): Album? {
        kotlin.runCatching {
            provideSpotifyApi().albums.getAlbum(id)
        }.onFailure {
            Log.d("SpotifyApiRequests", "Error: ${it.message}")
            return null
        }.onSuccess {
            return it
        }
        return null
    }

    @Provides
    @Singleton
    suspend fun providesGetAlbumById(id: String): Album? {
        return getAlbumById(id)
    }

    private suspend fun getAudioFeatures(id: String): AudioFeatures? {
        kotlin.runCatching {
            provideSpotifyApi().tracks.getAudioFeatures(id)
        }.onFailure {
            Log.d("SpotifyApiRequests", "Error: ${it.message}")
        }.onSuccess {
            return it
        }
        return null
    }

    @Provides
    @Singleton
    suspend fun providesGetAudioFeatures(id: String): AudioFeatures? {
        return getAudioFeatures(id)
    }

    private suspend fun getArtistTopTracks(artistId: String): List<Track>? {
        val artist = provideGetArtistById(artistId)
        return artist?.let {
            kotlin.runCatching {
                provideSpotifyApi().artists.getArtistTopTracks(artistId, Market.US)
            }.onFailure {
                Log.d("SpotifyApiRequests", "Error: ${it.message}")
                null
            }.getOrNull()
        }
    }

    @Provides
    @Singleton
    suspend fun providesGetArtistTopTracks(id: String): List<Track>? {
        return getArtistTopTracks(id)
    }

    private suspend fun getArtistAlbums(artistId: String): PagingObject<SimpleAlbum>? {
        val artist = provideGetArtistById(artistId)
        return artist?.let {
            kotlin.runCatching {
                provideSpotifyApi().artists.getArtistAlbums(artist=artistId, market=Market.US, limit=20)
            }.onFailure {
                Log.d("SpotifyApiRequests", "Error: ${it.message}")
                null
            }.getOrNull()
        }
    }

    @Provides
    @Singleton
    suspend fun providesGetArtistAlbums(id: String): PagingObject<SimpleAlbum>? {
        return getArtistAlbums(id)
    }
    
}