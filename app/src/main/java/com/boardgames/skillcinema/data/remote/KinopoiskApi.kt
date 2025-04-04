package com.boardgames.skillcinema.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface KinopoiskApi {

    @GET("/api/v2.2/films/top")
    suspend fun getTopMovies(
        @Query("type") type: String = "TOP_250_BEST_FILMS",
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("/api/v2.2/films/top")
    suspend fun getPopularMovies(
        @Query("type") type: String = "TOP_100_POPULAR_FILMS",
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("/api/v2.2/films/premieres")
    suspend fun getPremieres(
        @Query("year") year: Int,
        @Query("month") month: String
    ): MovieResponse

    @GET("/api/v2.2/films/filters")
    suspend fun getGenres(): GenreResponse

    @GET("/api/v2.2/films/filters")
    suspend fun getCountries(): CountryResponse

    @GET("/api/v2.2/films/{filmId}")
    suspend fun getMovie(
        @Path("filmId") filmId: Int
    ): Movie

    @GET("/api/v2.2/films/{id}/images")
    suspend fun getMovieImages(
        @Path("id") id: Int,
        @Query("type") type: String
    ): GalleryResponse

    @GET("/api/v2.2/films/{id}/seasons")
    suspend fun getSeriesEpisodes(
        @Path("id") movieId: Int
    ): SeriesEpisodesResponse

    @GET("/api/v2.2/films/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: Int
    ): MovieDetailsResponse

    @GET("/api/v1/staff/{id}")
    suspend fun getPersonDetail(
        @Path("id") personId: Int
    ): PersonDetailResponse

    @GET("/api/v1/persons")
    suspend fun searchPersons(
        @Query("name") name: String,
        @Query("page") page: Int
    ): PersonSearchResponse

    @GET("/api/v1/staff")
    suspend fun getMovieCast(
        @Query("filmId") filmId: Int
    ): List<CastResponse>

    @GET("/api/v1/staff")
    suspend fun getCrew(
        @Query("filmId") filmId: Int
    ): List<CrewResponse>

    @GET("/api/v2.2/films/{id}/images")
    suspend fun getMovieGallery(
        @Path("id") id: Int
    ): GalleryResponse

    @GET("/api/v2.2/films/{id}/similars")
    suspend fun getSimilarMovies(
        @Path("id") id: Int
    ): SimilarMoviesResponse

    @GET("/api/v2.2/films")
    suspend fun searchMovies(
        @Query("keyword") keyword: String,
        @Query("type") type: String? = null,
        @Query("countries") countries: String? = null,
        @Query("genres") genres: String? = null,
        @Query("yearFrom") yearFrom: Int? = null,
        @Query("yearTo") yearTo: Int? = null,
        @Query("ratingFrom") ratingFrom: Int? = null,
        @Query("ratingTo") ratingTo: Int? = null,
        @Query("order") order: String = "RATING",
        @Query("page") page: Int = 1
    ): MovieResponse
}
