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

package user;

import fr.nicolasgille.medialibs.core.user.User;
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
@RequestMapping(name = "/api/v1/services/users")
public class UserRestController {

    /**
     * Help on debugging.
     *
     * @since 1.0
     */
    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class.getPackage().getName());

    /**
     * Repository to manage entity on persistent system.
     *
     * @since 1.0
     */
    @Autowired
    private UserRepository userRepository;

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
        logger.info("Get all medias on persistent system");
        List<User> users = this.userRepository.findAll();

        if (users == null || users.isEmpty()) {
            logger.info("List of users is empty");
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        logger.info("Return the list of all users found.");
        return new ResponseEntity<List>(users, HttpStatus.OK);
    }

    /**
     * Get one user from system.
     *
     * @param id
     *  Identifier of requested user.
     * @return
     *  A ResponseEntity with user and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") long id) {
        User user = this.userRepository.findOne(id);

        if (user == null) {
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    /**
     * Add new user on persistent system.
     *
     * @param user
     *  User to insert on system.
     * @param uriBuilder
     *  Uri to redirect user on user page after creation.
     * @return
     *  A ResponseEntity with user and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @PostMapping("/")
    public ResponseEntity<?> add(@RequestBody User user, UriComponentsBuilder uriBuilder) {
        logger.info("Insert user {}", user);

        // @Todo : Add method findByEmail to check presence of user before insertion and return CONFLICT error status.

        HttpHeaders header = new HttpHeaders();
        this.userRepository.save(user);
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/users/{id}")
                        .buildAndExpand(user.getId())
                        .toUri());

        return new ResponseEntity<String>(header, HttpStatus.CREATED);
    }

    /**
     * Update the information about one precise user.
     *
     * @param id
     *  Identifier of the user.
     * @param updatedUser
     *  Information about the new user information.
     * @param uriBuilder
     *  Uri to redirect user on user page update.
     * @return
     *  A ResponseEntity with user and/or http code status about error during process.
     * @since 1.0
     * @version 1.0
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id,
                                    @RequestBody User updatedUser,
                                    UriComponentsBuilder uriBuilder) {
        logger.info("Update user {}", updatedUser);
        User userUpdated = this.userRepository.findOne(id);
        if (userUpdated== null) {
            logger.info("User with id {} not found on system", id);
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        userUpdated = new User();
        userUpdated.setId(id);
        userUpdated.setEmail(updatedUser.getEmail());
        userUpdated.setPassword(updatedUser.getPassword());
        userUpdated.setRole(updatedUser.getRole());
        this.userRepository.save(userUpdated);

        logger.info("User {} update on system", userUpdated);
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/users/{id}")
                        .buildAndExpand(id)
                        .toUri());
        return new ResponseEntity<Object>(header, HttpStatus.OK);
    }

    /**
     * Remove a precise user with his identifier.
     *
     * @param id
     *  Identifier of the user to remove from system.
     * @param uriBuilder
     *  Uri to redirect user to the page who contains all users.
     * @return
     *  A ResponseEntity with http code status to indicate the result of the process.
     * @since 1.0
     * @version 1.0
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id, UriComponentsBuilder uriBuilder) {
        logger.info("Delete user with id : {}", id);
        User userDeleted = this.userRepository.findOne(id);
        if (userDeleted == null) {
            logger.info("User with id {} not found", id);
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }

        this.userRepository.delete(id);

        logger.info("User {} is now deleted", userDeleted);
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                uriBuilder
                        .path("/api/v1/services/users/")
                        .buildAndExpand(id)
                        .toUri());
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
}
