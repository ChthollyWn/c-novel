package xyz.chthollywn.cnovel.websocket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.chthollywn.cnovel.common.enums.MessageTypeEnums;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {
    private MessageTypeEnums fromType;
    private String fromId;
    private MessageTypeEnums toType;
    private String toId;
    private String content;
}
