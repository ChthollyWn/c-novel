package xyz.chthollywn.cnovel.websocket.hander;

import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.websocket.Session;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xyz.chthollywn.cnovel.common.enums.MessageTypeEnums;
import xyz.chthollywn.cnovel.common.resp.FacadeException;
import xyz.chthollywn.cnovel.common.resp.PageDTO;
import xyz.chthollywn.cnovel.common.resp.ResponseDTO;
import xyz.chthollywn.cnovel.common.resp.ResponseException;
import xyz.chthollywn.cnovel.common.util.JsonUtil;
import xyz.chthollywn.cnovel.dao.facade.fanqie.entity.BookDetailResponse;
import xyz.chthollywn.cnovel.dao.facade.fanqie.entity.ChapterFullResponse;
import xyz.chthollywn.cnovel.dao.facade.fanqie.entity.SearchBookResponse;
import xyz.chthollywn.cnovel.service.fanqie.FanqieAccountService;
import xyz.chthollywn.cnovel.service.fanqie.FanqieNovelService;
import xyz.chthollywn.cnovel.strategy.cache.CacheService;
import xyz.chthollywn.cnovel.strategy.cache.CacheStrategy;
import xyz.chthollywn.cnovel.websocket.entity.MessageEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FanqieChannelHandler {
    @Resource
    private CacheService cacheService;
    @Resource
    private FanqieAccountService fanqieAccountService;
    @Resource
    private FanqieNovelService fanqieNovelService;

    private static final ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();
    private CacheStrategy cacheStrategy;

    private static final List<String> COMMAND_TEST = Lists.newArrayList("/test", "/TEST", "/测试");
    private static final List<String> COMMAND_HELP = Lists.newArrayList("/help", "/menu", "/帮助", "/菜单");
    private static final List<String> COMMAND_LOGIN_QRCODE = Lists.newArrayList("/login", "/qrcode", "/登录", "/二维码登录");
    private static final List<String> COMMAND_CHECK_USER = Lists.newArrayList("/check", "/check-user", "校验用户有效性");
    private static final List<String> COMMAND_BOOK_SEARCH = Lists.newArrayList("/search", "/book-search", "/搜书");
    private static final List<String> COMMAND_BOOK_DETAIL = Lists.newArrayList("/detail", "/book-detail", "/详情");
    private static final List<String> COMMAND_CHAPTER_FULL = Lists.newArrayList("/full", "/chapter-full", "/read", "/读书");

    @PostConstruct
    public void init() {
        this.cacheStrategy = cacheService.getCacheStrategy();
    }

    public void setSession(String userId, Session session) {
        sessionMap.put(userId, session);
    }

    public void removeSession(String userId) throws IOException {
        Session session = sessionMap.get(userId);
        if (session != null)
            session.close();
        sessionMap.remove(userId);
    }

    public void sendMessage(Session session, ResponseDTO responseDTO) {
        String json = JsonUtil.toJson(responseDTO);
        try {
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public void sendMessage(String userId, ResponseDTO responseDTO) {
        Session session = sessionMap.get(userId);
        if (session == null)
            throw new ResponseException("会话不存在");
        sendMessage(session, responseDTO);
    }

    public void sendMessageAndSave(String userId, ResponseDTO responseDTO) {
        Session session = sessionMap.get(userId);
        if (session == null)
            throw new ResponseException("会话不存在");
        sendMessage(session, responseDTO);
        saveServerToClientMessage(userId, responseDTO);
    }

    public void saveMessage(String userId, MessageEntity messageEntity) {
        String json = JsonUtil.toJson(messageEntity);
        cacheStrategy.saveMessage(userId, json);
    }

    public void saveClientToServerMessage(String userId, String message) {
        saveMessage(userId, new MessageEntity(
                MessageTypeEnums.Client, userId, MessageTypeEnums.Server, null, message));
    }

    public void saveServerToClientMessage(String userId, ResponseDTO responseDTO) {
        String json = JsonUtil.toJson(responseDTO);
        saveMessage(userId, new MessageEntity(
                MessageTypeEnums.Server, null, MessageTypeEnums.Client, userId, json));
    }

    public List<MessageEntity> getMessages(String userId) {
        List<String> messages = cacheStrategy.getMessages(userId);
        if (CollectionUtils.isEmpty(messages)) return Lists.newArrayList();
        List<MessageEntity> messageEntities = Lists.newArrayList();
        for (String message : messages) {
            MessageEntity entity = JsonUtil.toObj(message, MessageEntity.class);
            messageEntities.add(entity);
        }
        return messageEntities;
    }

    // socket初始化方法
    public void openInit(String userId, Session session) throws IOException {
        // 新登录的会话会挤掉之前的会话
        Session oldSession = sessionMap.get(userId);
        if (oldSession != null) {
            sendMessage(oldSession, ResponseDTO.socketSuccess(ResponseDTO.TEXT, "账号在另一地点登录，连接断开"));
            removeSession(userId);
        }
        setSession(userId, session);

        List<MessageEntity> messages = getMessages(userId);
        if (!CollectionUtils.isEmpty(messages)) {
            // 历史记录不为空则发送历史记录
            sendMessage(session, ResponseDTO.socketSuccess(ResponseDTO.HISTORY, messages));
        } else {
            // 历史记录为空发送帮助列表
            sendMenu(userId);
        }
    }

    public void sendMenu(String userId) {
        String menu = """
                <table>
                    <tr>
                      <td>欢迎使用C-Novel</td>
                      <td></td>
                    </tr>
                    <tr>
                      <td>请使用 [/命令 参数] 的格式发送命令</td>
                      <td></td>
                    </tr>
                    <tr>
                      <td>[/test]</td>
                      <td>测试websocket工作是否正常</td>
                    </tr>
                    <tr>
                      <td>[/help]</td>
                      <td>获取帮助列表</td>
                    </tr>
                    <tr>
                      <td>[/login]</td>
                      <td>通过二维码登录番茄账号</td>
                    </tr>
                    <tr>
                      <td>[/search [queryWord]]</td>
                      <td>通过关键字查询书籍</td>
                    </tr>
                    <tr>
                      <td>[/detail [bookId]]</td>
                      <td>通过书籍ID获取书籍详情</td>
                    </tr>
                    <tr>
                      <td>[/full [itemId]]</td>
                      <td>通过itemId阅读单章小说</td>
                    </tr>
              </table>
              """;
        sendMessageAndSave(userId, ResponseDTO.socketSuccess(ResponseDTO.TEXT, menu));
    }

    public void onHandler(String userId, String message) {
        // 接收到的所有消息都先保存到消息列表并向客户端发送一次message
        saveClientToServerMessage(userId, message);
        MessageEntity messageEntity = new MessageEntity(
                MessageTypeEnums.Client, userId, MessageTypeEnums.Server, null, message);
        sendMessage(userId, ResponseDTO.socketSuccess(ResponseDTO.MESSAGE, messageEntity));

        if (StringUtils.isBlank(message)) {
            sendMessageAndSave(userId, ResponseDTO.socketError("无效的命令"));
            return;
        }

        // 所有命令都分为 [/command value] 格式
        ArrayList<String> strings = Lists.newArrayList(message.split(" "));
        if (!strings.get(0).startsWith("/")) {
            sendMessageAndSave(userId, ResponseDTO.socketError("无效的命令"));
            return;
        }

        String command = strings.get(0);
        strings.remove(0);

        try {
            if (COMMAND_TEST.contains(command)) {
                // 测试成功
                sendMessageAndSave(userId, ResponseDTO.socketSuccess(ResponseDTO.TEXT, "测试成功"));
            } else if (COMMAND_HELP.contains(command)) {
                // 发送菜单
                sendMenu(userId);
            } else if (COMMAND_LOGIN_QRCODE.contains(command)) {
                // 发送登录二维码的base64编码
                String base64Str = fanqieAccountService.getQrcodeBase64Str(userId);
                sendMessageAndSave(userId, ResponseDTO.socketSuccess(ResponseDTO.IMG_BASE64, base64Str));
                sendMessageAndSave(userId, ResponseDTO.socketSuccess(ResponseDTO.TEXT, "二维码生成成功，请及时扫码登录"));
            } else if (COMMAND_CHECK_USER.contains(command)) {
                Boolean isVip = fanqieAccountService.checkUserIsVip();
                if (isVip) {
                    sendMessageAndSave(userId, ResponseDTO.socketSuccess(ResponseDTO.TEXT, "当前用户有效！"));
                } else {
                    sendMessageAndSave(userId, ResponseDTO.socketError("当前用户无效，请重新扫码登录！"));
                }
            } else if (COMMAND_BOOK_SEARCH.contains(command)) {
                // 书籍搜索
                String queryWord = null;
                Integer pageIndex = null;
                if (!strings.isEmpty()) queryWord = strings.get(0);
                if (strings.size() > 1) pageIndex = Integer.valueOf(strings.get(1));
                PageDTO<SearchBookResponse.BookData> pageDTO = fanqieNovelService.searchBookPage(pageIndex, queryWord);
                sendMessageAndSave(userId, ResponseDTO.socketSuccess(ResponseDTO.BOOK_PAGE, pageDTO));
            } else if (COMMAND_BOOK_DETAIL.contains(command)) {
                String bookId = null;
                if (!strings.isEmpty()) bookId = strings.get(0);
                BookDetailResponse.ResponseData responseData = fanqieNovelService.bookDetail(bookId);
                sendMessageAndSave(userId, ResponseDTO.socketSuccess(ResponseDTO.BOOK_DETAIL, responseData));
            } else if (COMMAND_CHAPTER_FULL.contains(command)) {
                String itemId = null;
                if (!strings.isEmpty()) itemId = strings.get(0);
                ChapterFullResponse.ResponseData responseData = fanqieNovelService.chapterFull(itemId);
                sendMessageAndSave(userId, ResponseDTO.socketSuccess(ResponseDTO.CHAPTER_FULL, responseData));
            } else {
                sendMessageAndSave(userId, ResponseDTO.socketError("无效的命令"));
            }
        } catch (FacadeException e) {
            sendMessageAndSave(userId, ResponseDTO.socketError(e.getMessage()));
        }
    }

}
