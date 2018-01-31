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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = MediaApplication.class
)
public class MediaRestControllerTest {

    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int serverPort;

    @Before
    public void setUp() {
        this.restTemplate = new TestRestTemplate();
    }

    @Test
    public void givenMedia_whenInsert_thenGetOkAndRedirectUri() {
        Media m = new Media();
        m.setName("Title");

        ResponseEntity<Media> responseEntity = restTemplate.postForEntity(
                "http://localhost:"  + serverPort + "/api/v1/services/medias/",
                m, Media.class
        );

        Media media = responseEntity.getBody();
        assertThat(responseEntity.getStatusCode()).isSameAs(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getLocation().toString()).isEqualTo("/api/v1/services/medias/");
        assertThat(media.getName()).isEqualTo("Title");
    }
}