package com.taxi.sb.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    ResponseEntity<JsonResponse> handleHttpMediaTypeNotSupportedException(HttpServletRequest request, Throwable ex) {
        return new ResponseEntity<>(new JsonResponse("json data required", 415), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    ResponseEntity<JsonResponse> handleHttpMessageNotReadableException(HttpServletRequest request, Throwable ex) {
        return new ResponseEntity<>(new JsonResponse("malformed submission",400),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    ResponseEntity<JsonResponse> handleGenericException(Throwable ex) {
        return new ResponseEntity<>(new JsonResponse("internal error",500),HttpStatus.INTERNAL_SERVER_ERROR);
    }

}