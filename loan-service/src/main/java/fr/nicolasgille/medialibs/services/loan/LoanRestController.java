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

package fr.nicolasgille.medialibs.services.loan;

import fr.nicolasgille.medialibs.core.loan.Loan;
import fr.nicolasgille.medialibs.core.loan.LoanRepository;
import fr.nicolasgille.medialibs.core.media.MediaRepository;
import fr.nicolasgille.medialibs.core.user.UserRepository;
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
@RequestMapping(name = "/api/v1/services/loans")
public class LoanRestController {

    /**
     * Help on debugging.
     *
     * @since 1.0
     */
    private static final Logger logger = LoggerFactory.getLogger(LoanRestController.class.getPackage().getName());

    /**
     * Repository to manage entity on persistent system.
     *
     * @since 1.0
     */
    @Autowired
    private LoanRepository loanRepository;
    private MediaRepository mediaRepository;
    private UserRepository userRepository;


    /**
     * Get all loans from system.
     *
     * @return
     *  A ResponseEntity with content and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        logger.info("Get all loans on persistent system");
        List<Loan> loans = this.loanRepository.findAll();

        if (loans == null || loans.isEmpty()) {
            logger.info("List of loans is empty");
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        logger.info("Return the list of all loans found.");
        return new ResponseEntity<List>(loans, HttpStatus.OK);
    }

    /**
     * Get one loan from system.
     *
     * @param id
     *  Identifier of requested loan.
     * @return
     *  A ResponseEntity with loan and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getLoan(@PathVariable("id") long id) {
        Loan loan = this.loanRepository.findOne(id);

        if (loan == null) {
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<Loan>(loan, HttpStatus.OK);
    }

    /**
     * Add new loan on persistent system.
     *
     * @param loan
     *  Loan to insert on system.
     * @param uriBuilder
     *  Uri to redirect user on loan page after creation.
     * @return
     *  A ResponseEntity with loan and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @PostMapping(value = "/")
    public ResponseEntity<?> add(@RequestBody Loan loan, UriComponentsBuilder uriBuilder) {
        logger.info("Insert loan {}", loan);

        // @Todo : Add method findByUserIdOrMediaId, to check presence of user before insertion and return CONFLICT error status.
        if (this.loanRepository.findByBorrowerIdAndMediaId(loan.getBorrowerId(), loan.getMediaId()) == null) {
            logger.info("Loan already found on system");
            return new ResponseEntity<Object>(HttpStatus.CONFLICT);
        }

        HttpHeaders header = new HttpHeaders();
        this.loanRepository.save(loan);
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/loans/{id}")
                        .buildAndExpand(loan.getId())
                        .toUri());

        return new ResponseEntity<String>(header, HttpStatus.CREATED);
    }

    /**
     * Update the information about one precise loan.
     *
     * @param id
     *  Identifier of the loan.
     * @param updatedLoan
     *  Information about the new loan information.
     * @param uriBuilder
     *  Uri to redirect loan on user page update.
     * @return
     *  A ResponseEntity with loan and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id,
                                    @RequestBody Loan updatedLoan,
                                    UriComponentsBuilder uriBuilder) {
        logger.info("Update loan {}", updatedLoan);
        Loan loanUpdated = this.loanRepository.findOne(id);
        if (loanUpdated == null) {
            logger.info("Loan with id {} not found on system", id);
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        loanUpdated = new Loan();
        loanUpdated.setId(id);
        loanUpdated.setBorrowerId(updatedLoan.getBorrowerId());
        loanUpdated.setMediaId(updatedLoan.getMediaId());
        loanUpdated.setStartLoanDate(updatedLoan.getStartLoanDate());
        loanUpdated.setEndLoanDate(updatedLoan.getEndLoanDate());
        this.loanRepository.save(loanUpdated);

        logger.info("Loan {} update on system", loanUpdated);
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/loans/{id}")
                        .buildAndExpand(id)
                        .toUri());
        return new ResponseEntity<Object>(header, HttpStatus.OK);
    }

    /**
     * Remove a precise loan with his identifier.
     *
     * @param id
     *  Identifier of the loan to remove from system.
     * @param uriBuilder
     *  Uri to redirect loan to the page who contains all users.
     * @return
     *  A ResponseEntity with http code status to indicate the result of the process.
     * @since 1.0
     * @version 1.0
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id, UriComponentsBuilder uriBuilder) {
        logger.info("Delete loan with id : {}", id);
        Loan loanDeleted = this.loanRepository.findOne(id);
        if (loanDeleted == null) {
            logger.info("Loan with id {} not found", id);
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        this.userRepository.delete(id);

        logger.info("User {} is now deleted", loanDeleted);
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/loans/")
                        .buildAndExpand(id)
                        .toUri());
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
}
