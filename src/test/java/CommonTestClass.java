import api.ApiClient;
import api.ApiService;
import entities.Player;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.ArrayList;
import java.util.List;

import static constants.Messages.BEFORE_AFTER_CLASS_MESSAGE;

@Slf4j
public class CommonTestClass {
    protected static ThreadLocal<Player> supervisor = ThreadLocal.withInitial(() -> ApiService.getPlayerByPlayerId(1));
    protected ThreadLocal<List<Integer>> createdPlayerIds = ThreadLocal.withInitial(ArrayList::new);

    @BeforeClass
    public void setUp() {
        createdPlayerIds.get().clear();
        ApiService.getAllPlayers().stream()
                .filter(player -> player.id != 1)
                .forEach(player -> ApiClient.deletePlayer(player.id, supervisor.get().login));
        log.info(String.format(BEFORE_AFTER_CLASS_MESSAGE, "Before"));
    }

    @AfterClass
    public void cleanUp() {
        createdPlayerIds.get().forEach(id -> ApiClient.deletePlayer(id, supervisor.get().login));
        createdPlayerIds.get().clear();
        log.info(String.format(BEFORE_AFTER_CLASS_MESSAGE, "After"));
    }
}
