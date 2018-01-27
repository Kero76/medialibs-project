/*
 * MediaLibs Service.
 * Copyright (C) 2018 Nicolas GILLE
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.nicolasgille.medialibs.services.media;

import fr.nicolasgille.medialibs.core.media.Media;
import fr.nicolasgille.medialibs.core.media.MediaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @since MediaLibs Service 1.0
 * @version 1.0
 */
@RestController
@RequestMapping(name = "/api/v1/services/medias")
public class MediaRestController {

    /**
     * Help on debugging.
     *
     * @since 1.0
     */
    private static final Logger logger = LoggerFactory.getLogger(MediaRestController.class.getPackage().getName());

    @Autowired
    private MediaRepository mediaRepository;

    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        logger.info("Get all medias on persistent system");
        List<Media> medias = this.mediaRepository.findAll();

        if (medias == null || medias.isEmpty()) {
            logger.info("List of media is empty");
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        logger.info("Return the list of all medias found.");
        return new ResponseEntity<List>(medias, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMedia(@PathVariable("id") long id) {
        Media media = this.mediaRepository.findOne(id);

        if (media == null) {
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<Media>(media, HttpStatus.OK);
    }
}
