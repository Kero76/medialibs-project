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

package fr.nicolasgille.medialibs.services.stock;

import fr.nicolasgille.medialibs.core.stock.Stock;
import fr.nicolasgille.medialibs.core.stock.StockRepository;
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
@RequestMapping(name = "/api/v1/services/stocks")
public class StockRestController {

    /**
     * Help on debugging.
     *
     * @since 1.0
     */
    private static final Logger logger = LoggerFactory.getLogger(StockRestController.class.getPackage().getName());

    /**
     * Repository to manage entity on persistent system.
     *
     * @since 1.0
     */
    @Autowired
    private StockRepository stockRepository;


    /**
     * Get all users from system.
     *
     * @return
     *  A ResponseEntity with content and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        logger.info("Get all stocks on persistent system");
        List<Stock> stocks = this.stockRepository.findAll();

        if (stocks== null || stocks.isEmpty()) {
            logger.info("List of users is empty");
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        logger.info("Return the list of all stocks found.");
        return new ResponseEntity<List>(stocks, HttpStatus.OK);
    }

    /**
     * Get one stock from system.
     *
     * @param id
     *  Identifier of requested stock.
     * @return
     *  A ResponseEntity with stock and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStock(@PathVariable("id") long id) {
        Stock stock = this.stockRepository.findOne(id);

        if (stock == null) {
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<Stock>(stock, HttpStatus.OK);
    }

    /**
     * Add new stock on persistent system.
     *
     * @param stock
     *  Stock to insert on system.
     * @param uriBuilder
     *  Uri to redirect user on stock page after creation.
     * @return
     *  A ResponseEntity with user and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @PostMapping("/")
    public ResponseEntity<?> add(@RequestBody Stock stock, UriComponentsBuilder uriBuilder) {
        logger.info("Insert stock {}", stock);

        if (this.stockRepository.findByMediaId(stock.getMediaId()) == null) {
            logger.info("Stock already found on system.");
            return new ResponseEntity<Object>(HttpStatus.CONFLICT);
        }

        HttpHeaders header = new HttpHeaders();
        this.stockRepository.save(stock);
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/stocks/{id}")
                        .buildAndExpand(stock.getId())
                        .toUri());

        return new ResponseEntity<String>(header, HttpStatus.CREATED);
    }

    /**
     * Increment the current stock of the stock object.
     *
     * @param id
     *  Identifier of the stock.
     * @param updateStock
     *  Information about the stock to increment current stock value.
     * @param uriBuilder
     *  Uri to redirect user on stock page update.
     * @return
     *  A ResponseEntity with user and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @PutMapping("/{id}/increment")
    public ResponseEntity<?> increment(@PathVariable("id") long id,
                                    @RequestBody Stock updateStock,
                                    UriComponentsBuilder uriBuilder) {
        logger.info("Increment Stock {}", updateStock);
        Stock stockUpdated = this.stockRepository.findOne(id);
        if (stockUpdated == null) {
            logger.info("Stock with id {} not found on system", id);
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        if (updateStock.isFill()) {
            logger.info("Stock cannot be increment because the current stock is over the initial stock.");
            return new ResponseEntity<Object>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        stockUpdated = new Stock();
        stockUpdated.setId(id);
        stockUpdated.setCurrentStock(updateStock.getCurrentStock() + 1);
        stockUpdated.setInitialStock(updateStock.getInitialStock());
        stockUpdated.setMediaId(updateStock.getMediaId());
        this.stockRepository.save(stockUpdated);

        logger.info("Stock {} increment the current stock for the media {}", stockUpdated, stockUpdated.getMediaId());
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/stocks/{id}")
                        .buildAndExpand(id)
                        .toUri());
        return new ResponseEntity<Object>(header, HttpStatus.OK);
    }

    /**
     * Decrement the current stock of the stock object.
     *
     * @param id
     *  Identifier of the stock.
     * @param updateStock
     *  Information about the stock to decrement current stock value.
     * @param uriBuilder
     *  Uri to redirect user on stock page update.
     * @return
     *  A ResponseEntity with user and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @PutMapping("/{id}/decrement")
    public ResponseEntity<?> decrement(@PathVariable("id") long id,
                                    @RequestBody Stock updateStock,
                                    UriComponentsBuilder uriBuilder) {
        logger.info("Decrement Stock {}", updateStock);
        Stock stockUpdated = this.stockRepository.findOne(id);
        if (stockUpdated == null) {
            logger.info("Stock with id {} not found on system", id);
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        if (updateStock.isEmpty()) {
            logger.info("Stock cannot be decrement because the current stock is under zero.");
            return new ResponseEntity<Object>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        stockUpdated = new Stock();
        stockUpdated.setId(id);
        stockUpdated.setCurrentStock(updateStock.getCurrentStock() - 1);
        stockUpdated.setInitialStock(updateStock.getInitialStock());
        stockUpdated.setMediaId(updateStock.getMediaId());
        this.stockRepository.save(stockUpdated);

        logger.info("Stock {} decrement the current stock for the media {}", stockUpdated, stockUpdated.getMediaId());
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/stocks/{id}")
                        .buildAndExpand(id)
                        .toUri());
        return new ResponseEntity<Object>(header, HttpStatus.OK);
    }

    /**
     * Remove a precise stock with his identifier.
     *
     * @param id
     *  Identifier of the stock to remove from system.
     * @param uriBuilder
     *  Uri to redirect stock to the page who contains all stocks.
     * @return
     *  A ResponseEntity with http code status to indicate the result of the process.
     * @since 1.0
     * @version 1.0
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id, UriComponentsBuilder uriBuilder) {
        logger.info("Delete stock with id : {}", id);
        Stock stockDeleted = this.stockRepository.findOne(id);
        if (stockDeleted == null) {
            logger.info("User with id {} not found", id);
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        this.stockRepository.delete(id);

        logger.info("Stock {} is now deleted", stockDeleted);
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/stocks/")
                        .buildAndExpand(id)
                        .toUri());
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
}
