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

import com.vmware.tanzu.demos.springflix.webui.model.MovieDetails;
import com.vmware.tanzu.demos.springflix.webui.model.MovieService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Controller
class MovieDetailsController {
    private final Logger logger = LoggerFactory.getLogger(MovieController.class);
    private final MovieService movieService;

    MovieDetailsController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/movies/{region}/{movieId}")
    String movieDetails(@PathVariable("movieId") String movieId, Model model, HttpServletResponse resp) {
        model.addAttribute("movieId", movieId);

        final var cacheMinutes = 10;
        resp.setHeader(HttpHeaders.CACHE_CONTROL, CacheControl.maxAge(Duration.ofMinutes(cacheMinutes)).cachePublic().getHeaderValue());
        resp.setHeader(HttpHeaders.EXPIRES, DateTimeFormatter.RFC_1123_DATE_TIME.format(OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(cacheMinutes)));
        return "views/movie-details";
    }

    @ModelAttribute("region")
    String regionModel(@PathVariable(name = "region", required = false) String region) {
        return region;
    }

    @GetMapping("/movies/{region}/{movieId}/_data")
    String movieDetailsData(@PathVariable("movieId") String movieId, Model model) {
        MovieDetails movieDetails = null;
        try {
            movieDetails = movieService.getMovieDetails(movieId);
        } catch (Exception e) {
            logger.warn("Failed to load movie details: {}", movieId, e);
        }
        model.addAttribute("movie", movieDetails);
        return "data/movie-details";
    }
}
