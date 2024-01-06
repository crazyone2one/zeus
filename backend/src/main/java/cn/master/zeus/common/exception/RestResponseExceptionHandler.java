package cn.master.zeus.common.exception;

import cn.master.zeus.common.response.ResultHolder;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Created by 11's papa on 01/03/2024
 **/
@Slf4j
@RestControllerAdvice
public class RestResponseExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredJwtException.class)
    public String handleServletException(ExpiredJwtException exception) {
        log.error("JWT过期异常: {}", exception.getMessage());
        return "Token Expired";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultHolder handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return ResultHolder.error(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BusinessException.class)
    public ResultHolder handleBusinessException(BusinessException ex) {
        return ResultHolder.error(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BadSqlGrammarException.class)
    public ResultHolder handleBadSqlGrammarException(BadSqlGrammarException ex) {
        return ResultHolder.error("数据库操作异常");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ResultHolder handleAccessDeniedException(AccessDeniedException ex) {
        return ResultHolder.error("没有权限访问该资源");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResultHolder handleException(Exception ex) {
        return ResultHolder.error(ex.getMessage());
    }
}
