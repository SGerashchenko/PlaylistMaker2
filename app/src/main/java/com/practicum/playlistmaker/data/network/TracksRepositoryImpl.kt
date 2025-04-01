package com.practicum.playlistmaker.data.network

import NetworkClient
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.data.mapper.TrackMapper
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track


class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(term: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(term))

        return if (response.resultCode == 200) {
            val searchResponse = response as TracksSearchResponse
            searchResponse.results.map { TrackMapper.mapToDomain(it) }
        } else {
            emptyList()
        }
    }
}