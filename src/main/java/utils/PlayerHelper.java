package utils;

import api.ApiService;
import entities.Player;

import java.util.HashMap;
import java.util.Map;

import static constants.FieldConstants.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static utils.RandomGenerator.*;

public class PlayerHelper {
    public static Player createPlayerBySupervisor(String role, Player supervisor) {
        Map<String, Object> dataMap = generateDataForUser(role, EMPTY);
        Player player = ApiService.createPlayer(dataMap, supervisor.login);
        return player;
    }

    public static Map<String, Object> generateDataForUser(String role, String prefix) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(AGE, generateRandomAge());
        dataMap.put(GENDER, generateRandomGender());
        dataMap.put(LOGIN, generateRandomLogin(prefix));
        dataMap.put(PASSWORD, generateRandomPassword(prefix));
        dataMap.put(SCREEN_NAME, generateRandomScreenName(prefix));
        dataMap.put(ROLE, role.equals(RANDOM) ? generateRandomRole() : role);
        return dataMap;
    }

    public static Object getValueByKey(Player player, String key) {
        return switch (key) {
            case ID -> player.getId();
            case AGE -> player.getAge();
            case GENDER -> player.getGender();
            case LOGIN -> player.getLogin();
            case PASSWORD -> player.getPassword();
            case SCREEN_NAME -> player.getScreenName();
            case ROLE -> player.getRole();
            default -> null;
        };
    }
}
