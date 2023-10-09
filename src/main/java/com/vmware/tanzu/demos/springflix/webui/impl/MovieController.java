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

import com.vmware.tanzu.demos.springflix.webui.model.MovieService;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.List;

@Controller
class MovieController {
    private final List<String> validRegions = List.of("US", "FR");
    private final MovieService ms;

    MovieController(MovieService ms) {
        this.ms = ms;
    }

    @GetMapping("/movies")
    String moviesFromDefaultRegion(Model model) {
        final var region = validRegions.get(0);
        return "redirect:/movies/" + region;
    }

    @GetMapping("/movies/{region}")
    String moviesFromRegion(@PathVariable("region") String region, HttpServletResponse resp) {
        if (!validRegions.contains(region)) {
            throw new IllegalArgumentException("Invalid region: " + region);
        }
        final var cacheMinutes = 10;
        resp.setHeader(HttpHeaders.CACHE_CONTROL, CacheControl.maxAge(Duration.ofMinutes(cacheMinutes)).cachePublic().getHeaderValue());
        resp.setHeader(HttpHeaders.EXPIRES, DateTimeFormatter.RFC_1123_DATE_TIME.format(OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(cacheMinutes)));
        return "movies";
    }

    @GetMapping("/movies/{region}/_data")
    String moviesData(@PathVariable("region") String region, Model model) {
        model.addAttribute("movies", ms.getUpcomingMovies(region));
        return "data/upcoming-movies";
    }

    @ModelAttribute("region")
    String regionModel(@PathVariable(value = "region", required = false) String region) {
        return region;
    }

    @ModelAttribute("regions")
    List<String> regionsModel() {
        return validRegions;
    }
}
