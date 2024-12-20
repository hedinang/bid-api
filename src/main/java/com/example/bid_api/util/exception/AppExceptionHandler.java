package com.example.bid_api.util.exception;//package com.project_management.util.exception;

import com.example.bid_api.util.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class AppExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return fieldName + ": " + errorMessage;
                })
                .collect(Collectors.toList());

        BaseResponse<Object> errorResponse = new BaseResponse<>(400, "toast.validation.failed", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse<?>> handleServiceException(ServiceException e) {
        BaseResponse<?> response = new BaseResponse<>(e.getCode(), e.getMessage(), e.getData());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


//    @ExceptionHandler(ServiceException.class)
//    @ResponseStatus(HttpStatus.OK)
//    public BaseResponse<?> handleServiceException(ServiceException e, HttpServletResponse response) {
//        response.setStatus(e.getStatusCode().value());
//        Object data = e.getData();
//        return new BaseResponse<>(e.getCode(), e.getMessage(), data);
//    }
//
//    @ExceptionHandler(IOException.class)
//    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
//    public void exceptionHandler(IOException e) {
//        log.error(e.getMessage());
//
//    }
//
////    @ExceptionHandler(Exception.class)
////    @ResponseStatus(HttpStatus.OK)
////    public BaseResponse<?> all(Exception ex, HttpServletRequest req, HttpServletResponse response) {
////        log.error(ex.getMessage());
////        return new BaseResponse<>(500, ex.getMessage(), null);
////    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public BaseResponse<?> notValid(MethodArgumentNotValidException ex, HttpServletRequest request, HttpServletResponse response) {
//        List<String> errors = new ArrayList<>();
//        ex.getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));
//        Map<String, List<String>> result = new HashMap<>();
//        result.put("errors", errors);
//        int status = ex.getBody().getStatus();
//        return new BaseResponse<>(status, errors.stream().map(Object::toString)
//                .collect(Collectors.joining(", ")), null);
//
//    }
//
//    @ExceptionHandler(BadCredentialsException.class)
//    public BaseResponse<?> badCredentials(BadCredentialsException ex, HttpServletRequest request, HttpServletResponse response) {
//        return new BaseResponse<>(ErrorCode.E404.code(), "사용자 ID 또는 비빌번호가 틀렸습니다.다시 입력해 주세요", null);
//    }
}
