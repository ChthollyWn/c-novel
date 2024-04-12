package xyz.chthollywn.cnovel.rest.errorhandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import xyz.chthollywn.cnovel.common.resp.FacadeException;
import xyz.chthollywn.cnovel.common.resp.ResponseDTO;
import xyz.chthollywn.cnovel.common.resp.ResponseException;
import xyz.chthollywn.cnovel.common.util.IpUtil;
import xyz.chthollywn.cnovel.common.util.RequestUtil;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    //处理所有异常
    @ExceptionHandler(Exception.class)
    protected ResponseDTO handleGlobalException(Exception ex, HttpServletRequest request) {
        log.error("{} {}\n异常信息-[{}]\nIP-[{}]\n请求详情-[{}]",
                request.getMethod(), request.getRequestURI(), ex.getMessage(),
                IpUtil.getIpAddr(request), RequestUtil.getRequestParamsAndBody(request), ex);
        return ResponseDTO.error("系统繁忙，请稍后再试");
    }

    // 处理资源不存在异常
    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseDTO handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseDTO.error("资源不存在 " + ex.getResourcePath());
    }

    // 处理Controller层参数校验异常
    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseDTO handleArgErrException(MethodArgumentNotValidException ex) {
        String errMsg = null;
        if (ex.getBindingResult().hasErrors()) {
            errMsg = ex.getBindingResult().getAllErrors()
                    .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .filter(Strings::isNotBlank)
                    .collect(Collectors.joining(", "));
        }
        if (Strings.isBlank(errMsg)) {
            return ResponseDTO.error();
        } else {
            return ResponseDTO.error("参数异常: " + errMsg);
        }
    }

    // 处理Service层或其他层校验异常
    @ExceptionHandler({ConstraintViolationException.class})
    protected ResponseDTO handleConstrainViolationException(ConstraintViolationException ex) {
        if (CollectionUtils.isEmpty(ex.getConstraintViolations())) return ResponseDTO.error("校验异常: " + ex.getMessage());
        List<String> errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();
        return ResponseDTO.error("校验异常: " + errors);
    }

    // 处理http异常
    @ExceptionHandler({HttpMessageNotReadableException.class})
    protected ResponseDTO handleHttpErrException(HttpMessageNotReadableException ex) {
        String message = String.format("HTTP异常 [%s]", ex.getMessage());
        return ResponseDTO.error(message);
    }

    // 处理http方法异常
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseDTO handleHttpMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String message = String.format("HTTP方法异常 不支持[%s] 支持[%s]",
                ex.getMethod(), ex.getSupportedMethods() == null ? "无" :String.join(",", ex.getSupportedMethods()));
        return ResponseDTO.error(message);
    }

    // 处理自定义异常
    @ExceptionHandler(ResponseException.class)
    protected ResponseDTO handleResponseException(ResponseException ex) {
        return ResponseDTO.error(ex.getMessage());
    }

    // 处理第三方组件异常
    @ExceptionHandler(FacadeException.class)
    protected ResponseDTO handleFacadeException(FacadeException ex, HttpServletRequest request) {
        log.error("{} {}\n异常信息-[{}]\nIP-[{}]\n请求详情-[{}]",
                request.getMethod(), request.getRequestURI(), ex.getMessage(),
                IpUtil.getIpAddr(request), RequestUtil.getRequestParamsAndBody(request), ex);
        return ResponseDTO.error("外部组件异常，请联系管理员");
    }
}
