package com.practicum.playlistmaker.data.mapper

import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.models.Track

object TrackMapper {
    fun mapToDomain(dto: TrackDto): Track {
        return Track(
            trackId = dto.trackId,
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTime = dto.trackTimeMillis, // Переименование поля
            artworkUrl100 = dto.artworkUrl100 ?: "", // Обработка nullable
            previewUrl = dto.previewUrl,
            collectionName = dto.collectionName ?: "Unknown", // Обработка nullable
            releaseDate = dto.releaseDate ?: "Unknown",
            primaryGenreName = dto.primaryGenreName,
            country = dto.country
        )
    }

    fun mapToDomainList(dtoList: List<TrackDto>): List<Track> {
        return dtoList.map { mapToDomain(it) }
    }
}