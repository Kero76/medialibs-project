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

import fr.nicolasgille.medialibs.core.user.Role;
import fr.nicolasgille.medialibs.core.user.User;
import fr.nicolasgille.medialibs.core.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @since MediaLibs Service 1.0
 * @version 1.0
 */
@RestController
@RequestMapping(name = "/api/v1/services/auth")
public class AuthenticateRestController {

    @Autowired
    private UserRepository userRepository;

    static final Logger logger = LoggerFactory.getLogger(AuthenticateRestController.class);

    /**
     * Authenticate user on system.
     *
     * @param user
     *  User to check authentication.
     * @return
     *  The user authenticate on system.
     * @since 1.0
     * @version 1.0
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody User user) {
        logger.info("Start authenticate method with user {} : ", user);

        User userAuth = userRepository.findByEmail(user.getEmail());
        if (userAuth == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        if (userAuth.getPassword().equals(user.getPassword())) {
            logger.info("user '{}' found on database.", user.getEmail());
            return new ResponseEntity<User>(userAuth, HttpStatus.OK);
        }

        // Return an error 404.
        logger.error("You should not see this message, but if see it, call me to fix it ;)");
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    /**
     * Register user on system.
     *
     * @param user
     *  User to save on system.
     * @return
     *  The user saved on system.
     * @since 1.0
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        logger.info("Start register method with user {} : ", user);
        if (userRepository.findByEmail(user.getEmail()) == null) {
            logger.info("user '{}' already present on database.", user.getEmail());
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        // Create entity to insert on db.
        User userEntity = new User();
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        userEntity.setRole(Role.GUEST_ROLE);

        // Save user on db and return on home page.
        logger.info("User save on database.");
        User userRegister = userRepository.save(userEntity);

        return new ResponseEntity<User>(userRegister, HttpStatus.CREATED);
    }
}
