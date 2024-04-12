package xyz.chthollywn.cnovel.common.resp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseException extends RuntimeException {
    private final String message;
}