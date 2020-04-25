package com.taxi.sb.exceptions;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class.getName());

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    ResponseEntity<JsonResponse> handleHttpMediaTypeNotSupportedException(HttpServletRequest request, Throwable ex) {
        LOGGER.error(ExceptionUtils.getStackTrace(ex));
        return new ResponseEntity<>(new JsonResponse("json data required", 415), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    ResponseEntity<JsonResponse> handleHttpMessageNotReadableException(HttpServletRequest request, Throwable ex) {
        LOGGER.error(ExceptionUtils.getStackTrace(ex));
        return new ResponseEntity<>(new JsonResponse("malformed submission",400),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExecutionException.class)
    @ResponseBody
    ResponseEntity<JsonResponse> handleExecutionException(HttpServletRequest request, Throwable ex) {
        LOGGER.error(ExceptionUtils.getStackTrace(ex));
        return new ResponseEntity<>(new JsonResponse("no path exists for the request",400),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidMapException.class)
    @ResponseBody
    ResponseEntity<JsonResponse> handleInvalidMapException(HttpServletRequest request, Throwable ex) {
        LOGGER.error(ExceptionUtils.getStackTrace(ex));
        return new ResponseEntity<>(new JsonResponse("invalid map request",400),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    ResponseEntity<JsonResponse> handleGenericException(Throwable ex) {
        LOGGER.error(ExceptionUtils.getStackTrace(ex));
        return new ResponseEntity<>(new JsonResponse("processing failed",500),HttpStatus.INTERNAL_SERVER_ERROR);
    }

}