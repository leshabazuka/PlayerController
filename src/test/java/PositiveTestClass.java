import api.ApiService;
import api.ApiClient;
import entities.Player;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.*;

import static constants.Messages.ENTITY_EXPECTED_ACTUAL_ASSERT_MESSAGE;
import static constants.FieldConstants.*;
import static constants.Roles.*;
import static utils.PlayerHelper.*;
import static utils.RandomGenerator.generateRandomRole;

@Slf4j
public class PositiveTestClass extends CommonTestClass{

    /**
     * This test verifies all possible allowed combinations of UPDATING player by another (user/admin/supervisor),
     * as specified in the requirements document "Test interview doc_eng". The combinations of players being tested are:
     * 1. admin -> user;
     * 2. supervisor -> admin;
     * 3. supervisor -> user;
     * 4. user -> its own user account;
     * 5. admin -> its own admin account;
     * 6. supervisor -> its own supervisor account.
     * The test passes if the update is successful, otherwise, it fails.
     */
    @Test(dataProvider = "updatePlayerData")
    public void updatePlayer(String createRole, String updater, String updateRole, Integer idForUpdate) {
        Integer playerIdForUpdate = (createRole != null) ? ApiService.createPlayer(generateDataForUser(createRole, CREATE), supervisor.get().login).id : idForUpdate;
        Map<String, Object> updateData = generateDataForUser(updateRole, UPDATE);
        createdPlayerIds.get().add(ApiService.updatePlayer(updateData, updater, playerIdForUpdate).id);
        Player targetPlayer = ApiService.getPlayerByPlayerId(playerIdForUpdate);

        SoftAssert softAssert = new SoftAssert();
        updateData.entrySet().stream()
                .forEach(entry -> {
                    Object actualValue = getValueByKey(targetPlayer, entry.getKey());
                    Object expectedValue = entry.getValue();
                    String message = String.format(ENTITY_EXPECTED_ACTUAL_ASSERT_MESSAGE, entry.getKey(), expectedValue, actualValue);
                    log.info(message);
                    softAssert.assertEquals(actualValue, expectedValue, message);
                });
        softAssert.assertAll();
    }

    @DataProvider
    public Object[][] updatePlayerData() {
        Player admin = createPlayerBySupervisor(ADMIN.getRole(), supervisor.get());
        createdPlayerIds.get().add(admin.id);
        Player user = createPlayerBySupervisor(USER.getRole(), supervisor.get());
        createdPlayerIds.get().add(user.id);
        Player supervisor = ApiService.getPlayerByPlayerId(1);

        Object[][] roleEditorRoleIdSet = {
                {USER.getRole(), admin.login, USER.getRole(), null},
                {ADMIN.getRole(), supervisor.login, ADMIN.getRole(), null},
                {USER.getRole(), supervisor.login, USER.getRole(), null},
                {null, user.login, USER.getRole(), user.id},
                {null, admin.login, ADMIN.getRole(), admin.id},
                {null, supervisor.login, SUPERVISOR.getRole(), supervisor.id}
        };
        return roleEditorRoleIdSet;
    }

    /**
     * This test verifies all possible allowed combinations of DELETING player by another (user/admin/supervisor),
     * as specified in the requirements document "Test interview doc_eng". The combinations of players being tested are:
     * 1. supervisor -> admin;
     * 2. supervisor -> user;
     * 3. admin -> user;
     * 4. admin -> its own admin account;
     * The test passes if delete is successful, otherwise, it fails.
     */
    @Test(dataProvider = "deletePlayerData")
    public void deletePlayer(String role, String editorRemover, Integer id) {
        Integer playerIdForDelete = role == null ? id : ApiService.createPlayer(generateDataForUser(role, DELETE), supervisor.get().login).id;
        Response response = ApiClient.deletePlayer(playerIdForDelete, editorRemover);

        Integer actualStatus = response.getStatusCode();
        Integer expectedStatus = 204;
        String message = String.format(ENTITY_EXPECTED_ACTUAL_ASSERT_MESSAGE, STATUS, expectedStatus, actualStatus);
        log.info(message);
        Assert.assertEquals(actualStatus, expectedStatus, message);
    }

    @DataProvider
    public Object[][] deletePlayerData() {
        Player admin = createPlayerBySupervisor(ADMIN.getRole(), supervisor.get());
        createdPlayerIds.get().add(admin.id);
        Player supervisor = ApiService.getPlayerByPlayerId(1);

        Object[][] roleEditorIdSet = {
                {ADMIN.getRole(), supervisor.login, null},
                {USER.getRole(), supervisor.login, null},
                {USER.getRole(), admin.login, null},
                {null, admin.login, admin.id}
        };
        return roleEditorIdSet;
    }

    /**
     * This test verifies that an existing player can be RETRIEVED by their id through a request.
     * The test passes if receiving player by id is successful, otherwise, it fails.
     */
    @Test
    public void getPlayerByPlayerId() {
        Map<String, Object> randomData = generateDataForUser(generateRandomRole(), CREATE);
        Response response = ApiClient.getPlayerByPlayerId(ApiService.createPlayer(randomData, supervisor.get().login).id);
        createdPlayerIds.get().add(response.then().extract().as(Player.class).id);

        Integer actualStatus = response.getStatusCode();
        Integer expectedStatus = 200;
        String message = String.format(ENTITY_EXPECTED_ACTUAL_ASSERT_MESSAGE, STATUS, expectedStatus, actualStatus);
        log.info(message);
        Assert.assertEquals(actualStatus, expectedStatus, message);
    }

    /**
     * This test verifies all possible allowed combinations of CREATING player by another (user/admin/supervisor),
     * as specified in the requirements document "Test interview doc_eng". The combinations of players being tested are:
     * 1. supervisor -> admin;
     * 2. supervisor -> user;
     * 3. admin -> user;
     * The test passes if create is successful, otherwise, it fails.
     */
    @Test(dataProvider = "createPlayerData")
    public void createPlayer(String playerRole, String editorLogin) {
        Map<String, Object> randomDataForUser = generateDataForUser(playerRole, CREATE);
        Player player = ApiService.getPlayerByPlayerId(ApiService.createPlayer(randomDataForUser, editorLogin).id);
        createdPlayerIds.get().add(player.id);

        SoftAssert softAssert = new SoftAssert();
        randomDataForUser.entrySet().stream()
                .forEach(entry -> {
                    Object actualValue = getValueByKey(player, entry.getKey());
                    Object expectedValue = entry.getValue();
                    String message = String.format(ENTITY_EXPECTED_ACTUAL_ASSERT_MESSAGE, entry.getKey(), expectedValue, actualValue);
                    log.info(message);
                    softAssert.assertEquals(actualValue, expectedValue, message);
                });
        softAssert.assertAll();
    }

    @DataProvider
    public Object[][] createPlayerData() {
        Player admin = createPlayerBySupervisor(ADMIN.getRole(), supervisor.get());
        createdPlayerIds.get().add(admin.id);
        Player supervisor = ApiService.getPlayerByPlayerId(1);

        Object[][] roleEditorSet = {
                {ADMIN.getRole(), supervisor.login},
                {USER.getRole(), supervisor.login},
                {USER.getRole(), admin.login}
        };
        return roleEditorSet;
    }
}
