package xyz.chthollywn.cnovel.common.resp;

import lombok.Getter;

@Getter
public class FacadeException extends RuntimeException{
    public FacadeException(String message) {
        super("外部组件异常：" + message);
    }
}
