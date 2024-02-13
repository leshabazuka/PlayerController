package api;

import entities.Player;
import exceptions.FrameworkException;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static constants.Messages.CANNOT_CONVERT_OBJECT_EXCEPTION_MESSAGE;
import static constants.Messages.STATUS_EXCEPTION_MESSAGE;

public class ApiService {
    public static Player createPlayer(Map<String, Object> parameters, String editor) {
        Response response = ApiClient.createPlayer(parameters, editor);
        if (response.getStatusCode() != 200)
            throw new FrameworkException(String.format(STATUS_EXCEPTION_MESSAGE, response.getStatusCode()));
        try {
            return response
                    .then().log().all()
                    .extract().as(Player.class);
        } catch (Exception e) {
            throw new FrameworkException(String.format(CANNOT_CONVERT_OBJECT_EXCEPTION_MESSAGE, Player.class));
        }
    }

    public static List<Player> getAllPlayers() {
        Response response = ApiClient.getAllPlayers();
        if (response.getStatusCode() != 200)
            throw new FrameworkException(String.format(STATUS_EXCEPTION_MESSAGE, response.getStatusCode()));
        try {
            return response
                    .then().log().all()
                    .extract().body().jsonPath().getList("players", Player.class);
        } catch (Exception e) {
            throw new FrameworkException(String.format(CANNOT_CONVERT_OBJECT_EXCEPTION_MESSAGE, Player.class));
        }
    }

    public static Player getPlayerByPlayerId(int playerId) {
        Response response = ApiClient.getPlayerByPlayerId(playerId);
        if (response.getStatusCode() != 200)
            throw new FrameworkException(String.format(STATUS_EXCEPTION_MESSAGE, response.getStatusCode()));
        try {
            return response
                    .then().log().all()
                    .extract().as(Player.class);
        } catch (Exception e) {
            throw new FrameworkException(String.format(CANNOT_CONVERT_OBJECT_EXCEPTION_MESSAGE, Player.class));
        }
    }

    public static Player updatePlayer(Map<String, Object> fieldsToUpdate, String editor, Integer playerId) {
        Response response = ApiClient.updatePlayer(fieldsToUpdate, editor, playerId);
        if (response.getStatusCode() != 200) {
            throw new FrameworkException(String.format(STATUS_EXCEPTION_MESSAGE, response.getStatusCode()));
        }
        try {
            return response
                    .then().log().all()
                    .extract().as(Player.class);
        } catch (Exception e) {
            throw new FrameworkException(String.format(CANNOT_CONVERT_OBJECT_EXCEPTION_MESSAGE, Player.class));
        }
    }
}
