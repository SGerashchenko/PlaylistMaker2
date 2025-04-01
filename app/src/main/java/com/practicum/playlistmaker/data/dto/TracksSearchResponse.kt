package com.practicum.playlistmaker.data.dto

data class TracksSearchResponse(
    val searchType: String,
    val term: String,
    val results: List<TrackDto>
) : Response()