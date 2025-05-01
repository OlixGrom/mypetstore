package org.ecom.mypetstore.integration;

import org.ecom.mypetstore.exception.ExternalApiException;
import org.ecom.mypetstore.model.external.Pet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class PetStoreApiClient {

    private static final Logger logger = LoggerFactory.getLogger(PetStoreApiClient.class);

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    @Autowired
    public PetStoreApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public ResponseEntity<Pet> getPetById(Long petId) {
        String url = BASE_URL + "/pet/" + petId;
        logger.info("Отправляем GET запрос к URL: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Pet> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Pet.class
            );

            logger.info("Ответ получен: статус = {}, тело = {}", response.getStatusCode(), response.getBody());
            return response;

        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Питомец с ID {} не найден. Ответ сервера: {}", petId, e.getResponseBodyAsString());
            throw new ExternalApiException("Питомец не найден", e);

        } catch (HttpServerErrorException e) {
            logger.error("Ошибка сервера при обращении к внешнему API: {}", e.getResponseBodyAsString());
            throw new ExternalApiException("Ошибка сервера внешнего API", e);

        } catch (Exception e) {
            logger.error("Неизвестная ошибка при обращении к внешнему API", e);
            throw new ExternalApiException("Неизвестная ошибка при обращении к внешнему API", e);
        }
    }

    /*public ResponseEntity<Pet> getPetById(Long id) {
        return restTemplate.getForEntity("https://petstore.swagger.io/v2/pet/" + id, Pet.class);
    }*/
}
