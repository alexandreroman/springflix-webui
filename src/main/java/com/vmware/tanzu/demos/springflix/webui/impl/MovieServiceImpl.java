/*
 * Copyright (c) 2023 VMware, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vmware.tanzu.demos.springflix.webui.impl;

import com.vmware.tanzu.demos.springflix.webui.model.Movie;
import com.vmware.tanzu.demos.springflix.webui.model.MovieDetails;
import com.vmware.tanzu.demos.springflix.webui.model.MovieService;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Comparator;
import java.util.List;

@Service
class MovieServiceImpl implements MovieService {
    private final MovieClientService mcs;
    private final MovieTrailerClientService mtcs;

    MovieServiceImpl(MovieClientService mcs, MovieTrailerClientService mtcs) {
        this.mcs = mcs;
        this.mtcs = mtcs;
    }

    @Override
    public List<Movie> getUpcomingMovies(String region) {
        return mcs.getUpcomingMovies(region).stream()
                .map(m -> new Movie(m.id(), m.title(), m.releaseDate()))
                .sorted(Comparator.comparing(Movie::releaseDate))
                .toList();
    }

    @Override
    public MovieDetails getMovieDetails(String movieId) {
        final var m = mcs.getMovie(movieId);
        final var trailers = mtcs.getMovieTrailers(movieId);
        final URI trailerUri;
        if (trailers.trailers().isEmpty()) {
            trailerUri = null;
        } else {
            trailerUri = toYoutubeEmbed(trailers.trailers().get(0).videoUri());
        }
        return new MovieDetails(movieId, m.title(), m.releaseDate(), trailerUri);
    }

    private URI toYoutubeEmbed(URI trailerUri) {
        if (trailerUri.getHost().contains("youtube.com")) {
            return URI.create(trailerUri.toASCIIString().replace("/watch?v=", "/embed/"));
        }
        return trailerUri;
    }
}
