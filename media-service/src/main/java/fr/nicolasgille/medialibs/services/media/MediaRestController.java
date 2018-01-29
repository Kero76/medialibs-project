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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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

    /**
     * Repository to manage entity on persistent system.
     *
     * @see fr.nicolasgille.medialibs.core.media.MediaRepository
     * @since 1.0
     */
    @Autowired
    private MediaRepository mediaRepository;


    /**
     * Get all medias from system.
     *
     * @return
     *  A ResponseEntity with content and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
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

    /**
     * Get on media from system.
     *
     * @param id
     *  Identifier of requested media.
     * @return
     *  A ResponseEntity with media and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMedia(@PathVariable("id") long id) {
        Media media = this.mediaRepository.findOne(id);

        if (media == null) {
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<Media>(media, HttpStatus.OK);
    }

    /**
     * Add new media on persistent system.
     *
     * @param media
     *  Media to insert on system.
     * @param uriBuilder
     *  Uri to redirect user on media page after creation.
     * @return
     *  A ResponseEntity with media and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @PostMapping("/")
    public ResponseEntity<?> add(@RequestBody Media media, UriComponentsBuilder uriBuilder) {
        logger.info("Insert media {}", media);

        if (this.mediaRepository.findByNameAndReleaseDate(media.getName(), media.getReleaseDate()) == null) {
            logger.info("Media already found on system.");
            return new ResponseEntity<Object>(HttpStatus.CONFLICT);
        }

        this.mediaRepository.save(media);
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/medias/{id}")
                        .buildAndExpand(media.getId())
                        .toUri());

        return new ResponseEntity<String>(header, HttpStatus.CREATED);
    }

    /**
     * Update the information about one precise media.
     *
     * @param id
     *  Identifier of the media.
     * @param updatedMedia
     *  Information about the new media.
     * @param uriBuilder
     *  Uri to redirect user on media page update.
     * @return
     *  A ResponseEntity with media and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id,
                                    @RequestBody Media updatedMedia,
                                    UriComponentsBuilder uriBuilder) {
        logger.info("Update media {}", updatedMedia);
        Media mediaUpdated = this.mediaRepository.findOne(id);
        if (mediaUpdated == null) {
            logger.info("Media with id {} not found on system", id);
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        mediaUpdated = new Media();
        mediaUpdated.setId(id);
        mediaUpdated.setName(updatedMedia.getName());
        mediaUpdated.setDescription(updatedMedia.getDescription());
        mediaUpdated.setReleaseDate(updatedMedia.getReleaseDate());
        mediaUpdated.setSupports(updatedMedia.getSupports());
        this.mediaRepository.save(mediaUpdated);

        logger.info("Media {} insert on system", mediaUpdated);
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/medias/{id}")
                        .buildAndExpand(id)
                        .toUri());
        return new ResponseEntity<Object>(header, HttpStatus.OK);
    }

    /**
     * Remove a precise media with his identifier.
     *
     * @param id
     *  Identifier of the media to remove from system.
     * @param uriBuilder
     *  Uri to redirect user to the page who contains all medias.
     * @return
     *  A ResponseEntity with media and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id, UriComponentsBuilder uriBuilder) {
        logger.info("Delete media with id : {}", id);
        Media mediaDeleted = this.mediaRepository.findOne(id);
        if (mediaDeleted == null) {
            logger.info("Media with id {} not found", id);
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        this.mediaRepository.delete(id);

        logger.info("Media {} is now deleted", mediaDeleted);
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/medias/")
                        .buildAndExpand(id)
                        .toUri());
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
}
