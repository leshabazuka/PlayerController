import api.ApiService;
import api.ApiClient;
import entities.Player;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import utils.PlayerHelper;

import java.util.*;

import static constants.Messages.ENTITY_EXPECTED_ACTUAL_ASSERT_MESSAGE;
import static constants.FieldConstants.*;
import static constants.Roles.*;
import static utils.PlayerHelper.generateDataForUser;
import static utils.RandomGenerator.generateRandomIntNumber;
import static utils.RandomGenerator.generateRandomPassword;

@Slf4j
public class NegativeTestClass extends CommonTestClass {
    /**
     * This test verifies all possible disallowed combinations of UPDATING player by another (user/admin/supervisor),
     * as specified in the requirements document "Test interview doc_eng". The combinations of players being tested are:
     * 1. user -> user;
     * 2. user -> admin;
     * 3. admin -> user;
     * 4. user -> supervisor;
     * 5. admin -> supervisor;
     * The test passes if the update is unsuccessful (statusCode = 403), otherwise, it fails.
     */
    @Test(dataProvider = "updatePlayerData")
    public void updatePlayer(String createRole, String editorUpdater, String updateRole, Integer editorUpdatingEntityId) {
        Player playersCreation = (createRole == null) ? null : ApiService.createPlayer(generateDataForUser(createRole, CREATE), supervisor.get().login);
        if (createRole != null) {
            createdPlayerIds.get().add(playersCreation.id);
        }
        Response response = ApiClient.updatePlayer(generateDataForUser(updateRole, UPDATE), editorUpdater, (createRole != null) ? playersCreation.id : editorUpdatingEntityId);

        Integer actualStatus = response.getStatusCode();
        Integer expectedStatus = 403;
        String message = String.format(ENTITY_EXPECTED_ACTUAL_ASSERT_MESSAGE, STATUS, expectedStatus, actualStatus);
        log.info(message);
        Assert.assertEquals(actualStatus, expectedStatus, message);
    }

    @DataProvider
    public Object[][] updatePlayerData() {
        Player user = PlayerHelper.createPlayerBySupervisor(USER.getRole(), supervisor.get());
        createdPlayerIds.get().add(user.id);
        Player admin = PlayerHelper.createPlayerBySupervisor(ADMIN.getRole(), supervisor.get());
        createdPlayerIds.get().add(admin.id);

        Object[][] roleLoginRoleId = {
                {USER.getRole(), user.login, USER.getRole(), null},
                {ADMIN.getRole(), user.login, ADMIN.getRole(), null},
                {ADMIN.getRole(), admin.login, ADMIN.getRole(), null},
                {null, user.login, SUPERVISOR.getRole(), supervisor.get().id},
                {null, admin.login, SUPERVISOR.getRole(), supervisor.get().id}
        };
        return roleLoginRoleId;
    }

    /**
     * This test verifies all possible disallowed combinations of DELETING player by another (user/admin/supervisor),
     * as specified in the requirements document "Test interview doc_eng". The combinations of players being tested are:
     * 1. admin -> admin;
     * 2. user -> admin;
     * 3. user -> user;
     * 4. supervisor -> its own supervisor account;
     * 5. admin -> supervisor;
     * 6. user -> supervisor;
     * 7. user -> its own user account;
     * The test passes if delete is unsuccessful (statusCode = 403), otherwise, it fails.
     */
    @Test(dataProvider = "deletePlayerData")
    public void deletePlayer(String role, String editorRemover, Integer playerIdToDelete) {
        int createdPlayerId = playerIdToDelete != null ? playerIdToDelete : ApiService.createPlayer(generateDataForUser(role, DELETE), supervisor.get().login).id;
        Response response = ApiClient.deletePlayer(createdPlayerId, editorRemover);
        if (playerIdToDelete == null && response.getStatusCode() != 403) {
            createdPlayerIds.get().add(createdPlayerId);
        }

        Integer actualStatus = response.getStatusCode();
        Integer expectedStatus = 403;
        String message = String.format(ENTITY_EXPECTED_ACTUAL_ASSERT_MESSAGE, STATUS, expectedStatus, actualStatus);
        log.info(message);
        Assert.assertEquals(actualStatus, expectedStatus, message);
    }

    @DataProvider
    public Object[][] deletePlayerData() {
        Player user = PlayerHelper.createPlayerBySupervisor(USER.getRole(), supervisor.get());
        createdPlayerIds.get().add(user.id);
        Player admin = PlayerHelper.createPlayerBySupervisor(ADMIN.getRole(), supervisor.get());
        createdPlayerIds.get().add(admin.id);
        Player supervisor = ApiService.getPlayerByPlayerId(1);

        Object[][] roleLoginIdSet = {
                {ADMIN.getRole(), admin.login, null},
                {ADMIN.getRole(), user.login, null},
                {USER.getRole(), user.login, null},
                {null, supervisor.login, supervisor.id},
                {null, admin.login, supervisor.id},
                {null, user.login, supervisor.id},
                {null, user.login, user.id}
        };
        return roleLoginIdSet;
    }

    /**
     * This test verifies all possible prohibited combinations of fields and data for CREATING a player,
     * as specified in the requirements document "Test interview doc_eng". The combinations of fields and data being tested are:
     * 1. age -> 16 - checks the possibility of creating a player younger than 17 years old;
     * 2. age -> 60 - checks the possibility of creating a player older than 59 years old;
     * 3. login -> login of an already created player - checks the possibility of creating a player with a non-unique login;
     * 4. screenName -> screenName of an already created player - checks the possibility of creating a player with a non-unique screenName;
     * 5. password -> len<7 - checks the possibility of creating a player with a password shorter than 7 characters;
     * 6. password -> length>15spec!@#andрусскиеsymbols - checks the possibility of creating a player with a password longer than 15 characters, including non-Latin characters and digits;
     * 7. gender -> another - checks the possibility of creating a player with a gender different from male/female;
     * 8. role -> another - checks the possibility of creating a player with a role different from user/admin.
     * The test passes if player creating is unsuccessful (statusCode = 400), otherwise, it fails.
     */
    @Test(dataProvider = "provideDataForValidation")
    public void validateData(String key, Object value, Integer createdId) {
        Map<String, Object> testData = generateDataForUser(ADMIN.getRole(), CREATE);
        testData.put(key, value);
        Response response = ApiClient.createPlayer(testData, supervisor.get().login);

        SoftAssert softAssert = new SoftAssert();
        if (response.getStatusCode() < 400) {
            Integer id = response.then().extract().as(Player.class).id;
            createdPlayerIds.get().add(response.then().extract().as(Player.class).id);
            Integer expectedId = createdId;
            Integer actualId = id;
            String idMessage = String.format(ENTITY_EXPECTED_ACTUAL_ASSERT_MESSAGE, ID, expectedId, actualId);
            log.info(idMessage);
            softAssert.assertEquals(actualId, expectedId, idMessage);
        } else {
            Integer actualStatus = response.getStatusCode();
            Integer expectedStatus = 400;
            String message = String.format(ENTITY_EXPECTED_ACTUAL_ASSERT_MESSAGE, STATUS, expectedStatus, actualStatus);
            log.info(message);
            softAssert.assertEquals(actualStatus, expectedStatus, message);
        }
        softAssert.assertAll();
    }

    @DataProvider
    public Object[][] provideDataForValidation() {
        Player admin = PlayerHelper.createPlayerBySupervisor(ADMIN.getRole(), supervisor.get());
        createdPlayerIds.get().add(admin.id);
        Object[][] keyValueIdSet = {
                {AGE, 16, admin.id},
                {AGE, 60, admin.id},
                {LOGIN, admin.login, admin.id},
                {SCREEN_NAME, admin.screenName, admin.id},
                {PASSWORD, "len<7", admin.id},
                {PASSWORD, "length>15spec!@#andрусскиеsymbols", admin.id},
                {GENDER, "another", admin.id},
                {ROLE, "another", admin.id}
        };
        return keyValueIdSet;
    }

    /**
     * This test verifies that requests with incorrect or non-existent ids RETURN a response with specific data:
     * statusCode = 400
     * or
     * statusCode = 200 with Content-length = 0.
     */
    @Test(dataProvider = "getPlayerByPlayerIdData")
    public void getPlayerByPlayerId(Object id) {
        Response response = ApiClient.getPlayerByPlayerId(id);

        SoftAssert softAssert = new SoftAssert();
        Integer actualStatus = response.getStatusCode();
        Integer expectedStatus = id instanceof String ? 400 : 200;
        String statusMessage = String.format(ENTITY_EXPECTED_ACTUAL_ASSERT_MESSAGE, STATUS, expectedStatus, actualStatus);
        log.info(statusMessage);
        softAssert.assertEquals(actualStatus, expectedStatus, statusMessage);
        if (response.getStatusCode() == 200) {
            String actualValue = ApiClient.getPlayerByPlayerId(id).headers().getValue("Content-Length");
            String expectedValue = "0";
            String valueMessage = String.format(ENTITY_EXPECTED_ACTUAL_ASSERT_MESSAGE, STATUS, expectedValue, actualValue);
            log.info(valueMessage);
            softAssert.assertEquals(actualStatus, expectedStatus, valueMessage);
        }
        softAssert.assertAll();
    }

    @DataProvider
    public Object[][] getPlayerByPlayerIdData() {
        Object[][] idSet = {
                {generateRandomIntNumber(2)},
                {generateRandomPassword(RANDOM)}
        };
        return idSet;
    }

    /**
     * This test verifies all possible disallowed combinations of CREATING player by another (user/admin/supervisor),
     * as specified in the requirements document "Test interview doc_eng". The combinations of players being tested are:
     * 1. supervisor -> supervisor;
     * 2. admin -> supervisor;
     * 3. admin -> admin;
     * 4. user -> supervisor;
     * 5. user -> admin;
     * 6. user -> user;
     * The test passes if create is unsuccessful (statusCode = 400/403), otherwise, it fails.
     */
    @Test(dataProvider = "createPlayerData")
    public void createPlayer(String role, String editor) {
        Response response = ApiClient.createPlayer(generateDataForUser(role, CREATE), editor);
        if (response.getStatusCode() < 400) {
            createdPlayerIds.get().add(response.then().extract().as(Player.class).id);
        }

        Integer actualStatus = response.getStatusCode();
        Integer expectedStatus = actualStatus.equals(403) ? 403 : 400;
        String message = String.format(ENTITY_EXPECTED_ACTUAL_ASSERT_MESSAGE, STATUS, expectedStatus, actualStatus);
        log.info(message);
        Assert.assertEquals(actualStatus, expectedStatus, message);
    }

    @DataProvider
    public Object[][] createPlayerData() {
        Player user = PlayerHelper.createPlayerBySupervisor(USER.getRole(), supervisor.get());
        createdPlayerIds.get().add(user.id);
        Player admin = PlayerHelper.createPlayerBySupervisor(ADMIN.getRole(), supervisor.get());
        createdPlayerIds.get().add(admin.id);
        Player supervisor = ApiService.getPlayerByPlayerId(1);

        Object[][] roleLoginSet = {
                {SUPERVISOR.getRole(), supervisor.login},
                {SUPERVISOR.getRole(), admin.login},
                {ADMIN.getRole(), admin.login},
                {SUPERVISOR.getRole(), user.login},
                {ADMIN.getRole(), user.login},
                {USER.getRole(), user.login}
        };
        return roleLoginSet;
    }
}
