package xyz.chthollywn.cnovel.strategy.cache;

import java.util.List;


public interface CacheStrategy {
    void setFanqieSid_tt(String sid_tt);

    String getFanqieSid_tt();

    void saveMessage(String userId, String message);

    List<String> getMessages(String userId);
}
