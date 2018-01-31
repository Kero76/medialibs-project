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

package fr.nicolasgille.medialibs.services.advertiser;

import fr.nicolasgille.medialibs.core.advert.Advert;
import fr.nicolasgille.medialibs.core.advert.AdvertRepository;
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
@RequestMapping(name = "/api/v1/services/adverts")
public class AdvertRestController {

    @Autowired
    private AdvertRepository advertRepository;

    static final Logger logger = LoggerFactory.getLogger(AdvertApplication.class);



    /**
     * Get all adverts from system.
     *
     * @return
     *  A ResponseEntity with content and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        logger.info("Get all adverts on persistent system");
        List<Advert> adverts = this.advertRepository.findAll();

        if (adverts == null || adverts.isEmpty()) {
            logger.info("List of adverts is empty");
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        logger.info("Return the list of all adverts found.");
        return new ResponseEntity<List>(adverts, HttpStatus.OK);
    }

    /**
     * Get on advert from system.
     *
     * @param id
     *  Identifier of requested advert.
     * @return
     *  A ResponseEntity with advert and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdvert(@PathVariable("id") long id) {
        Advert advert = this.advertRepository.findOne(id);

        if (advert == null) {
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<Advert>(advert, HttpStatus.OK);
    }

    /**
     * Add new advert on persistent system.
     *
     * @param advert
     *  Advert to insert on system.
     * @param uriBuilder
     *  Uri to redirect user on advert page after creation.
     * @return
     *  A ResponseEntity with advert and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @PostMapping("/")
    public ResponseEntity<?> add(@RequestBody Advert advert, UriComponentsBuilder uriBuilder) {
        logger.info("Insert advert {}", advert);

        if (this.advertRepository.findByTitle(advert.getTitle()) == null) {
            logger.info("Advert already found on system.");
            return new ResponseEntity<Object>(HttpStatus.CONFLICT);
        }

        this.advertRepository.save(advert);
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/adverts/{id}")
                        .buildAndExpand(advert.getId())
                        .toUri());

        return new ResponseEntity<String>(header, HttpStatus.CREATED);
    }

    /**
     * Update the information about one precise advert.
     *
     * @param id
     *  Identifier of the advert.
     * @param updatedAdvert
     *  Information about the new advert.
     * @param uriBuilder
     *  Uri to redirect user on advert page update.
     * @return
     *  A ResponseEntity with advert and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id,
                                    @RequestBody Advert updatedAdvert,
                                    UriComponentsBuilder uriBuilder) {
        logger.info("Update advert {}", updatedAdvert);
        Advert advertUpdated = this.advertRepository.findOne(id);
        if (advertUpdated == null) {
            logger.info("Media with id {} not found on system", id);
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        advertUpdated = new Advert();
        advertUpdated.setId(id);
        advertUpdated.setTitle(updatedAdvert.getTitle());
        advertUpdated.setContent(updatedAdvert.getContent());
        advertUpdated.setAdvertDate(updatedAdvert.getAdvertDate());
        advertUpdated.setAdvertiserId(updatedAdvert.getAdvertiserId());
        this.advertRepository.save(advertUpdated);

        logger.info("Advert {} insert on system", advertUpdated);
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/adverts/{id}")
                        .buildAndExpand(id)
                        .toUri());
        return new ResponseEntity<Object>(header, HttpStatus.OK);
    }

    /**
     * Remove a precise advert with his identifier.
     *
     * @param id
     *  Identifier of the advert to remove from system.
     * @param uriBuilder
     *  Uri to redirect user to the page who contains all medias.
     * @return
     *  A ResponseEntity with advert and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id, UriComponentsBuilder uriBuilder) {
        logger.info("Delete advert with id : {}", id);
        Advert advertDeleted = this.advertRepository.findOne(id);
        if (advertDeleted == null) {
            logger.info("Advert with id {} not found", id);
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        this.advertRepository.delete(id);

        logger.info("Advert {} is now deleted", advertDeleted);
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/adverts/")
                        .buildAndExpand(id)
                        .toUri());
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
}
