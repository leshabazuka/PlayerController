package api;

import com.google.gson.Gson;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import utils.ConfigurationLoader;

import java.util.Map;

import static constants.FieldConstants.HOST;
import static constants.Messages.URL_STATUS_BODY_RESPONSE_MESSAGE;
import static constants.Messages.URL_STATUS_RESPONSE_MESSAGE;
import static io.restassured.RestAssured.given;

@Slf4j
public class ApiClient {
    private static final String BASE_HOST = ConfigurationLoader.getProperty(HOST);
    private static final String ALL_PLAYERS_URI = String.format("%s/player/get/all", BASE_HOST);
    private static final String PLAYER_CREATION_URI = String.format("%s/player/create/%%s", BASE_HOST);
    private static final String PLAYER_UPDATE_URI = String.format("%s/player/update/%%s/%%d", BASE_HOST);
    private static final String PLAYER_RECEIVING_URI = String.format("%s/player/get", BASE_HOST);
    private static final String PLAYER_DELETION_URI = String.format("%s/player/delete/%%s", BASE_HOST);
    private static final String REQUEST_BODY = "{\"playerId\": %d}";
    private static final String GET_PLAYER_REQUEST_BODY = "{\"playerId\": %s}";

    private static final RequestSpecification REQUEST_SPECIFICATION = new RequestSpecBuilder()
            .addFilter(new AllureRestAssured())
            .build();

    public static Response getAllPlayers(){
        Response response = given().spec(REQUEST_SPECIFICATION)
                .get(ALL_PLAYERS_URI);
        log.info(String.format(URL_STATUS_RESPONSE_MESSAGE, ALL_PLAYERS_URI, response.getStatusCode()));
        return response;
    }

    public static Response createPlayer(Map<String, Object> body, String editor){
        String uri = String.format(PLAYER_CREATION_URI, editor);
        Response response = given().spec(REQUEST_SPECIFICATION)
                .queryParams(body)
                .get(uri);
        log.info(String.format(URL_STATUS_BODY_RESPONSE_MESSAGE, uri, response.getStatusCode(), body.toString()));
        return response;
    }

    public static Response updatePlayer(Map<String, Object> body, String editor, Integer playerId){
        String uri = String.format(PLAYER_UPDATE_URI, editor, playerId);
        Response response = given().spec(REQUEST_SPECIFICATION)
                .contentType(ContentType.JSON)
                .body(new Gson().toJson(body))
                .patch(uri);
        log.info(String.format(URL_STATUS_BODY_RESPONSE_MESSAGE, uri, response.getStatusCode(), body.toString()));
        return response;
    }

    public static Response getPlayerByPlayerId(Object id) {
        String uri = String.format(GET_PLAYER_REQUEST_BODY, id);
        Response response = given().spec(REQUEST_SPECIFICATION)
                .contentType(ContentType.JSON)
                .body(uri)
                .post(PLAYER_RECEIVING_URI);
        log.info(String.format(URL_STATUS_RESPONSE_MESSAGE, PLAYER_RECEIVING_URI+id.toString(), response.getStatusCode()));
        return response;
    }

    public static Response deletePlayer(int playerId, String editor) {
        String uri = String.format(PLAYER_DELETION_URI, editor);
        Response response = given().spec(REQUEST_SPECIFICATION)
                .contentType(ContentType.JSON)
                .body(String.format(REQUEST_BODY, playerId))
                .delete(uri);
        log.info(String.format(URL_STATUS_RESPONSE_MESSAGE, uri, response.getStatusCode()));
        return response;
    }
}
