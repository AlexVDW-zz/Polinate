package za.co.pollinate.order_management.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import za.co.pollinate.order_management.dto.BaseResponse;
import za.co.pollinate.order_management.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handleNotFoundError(NotFoundException e){
        return errorResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleNotFoundError(MethodArgumentNotValidException e){
        String validationFailures = e.getBindingResult().getFieldErrors()
                                .stream().map(
                                    x -> x.getField() + ": " + x.getDefaultMessage())
                                .collect(Collectors.joining(", "));
        return errorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", validationFailures);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleUnexpectedError(Exception e){
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred");
    }

    private ResponseEntity<BaseResponse<Object>> errorResponse(HttpStatus status, String errorCode, String errorMessage){
        ErrorResponse errorResponse = new ErrorResponse(errorCode, errorMessage);
        return ResponseEntity.status(status).body(new BaseResponse<>(errorResponse, null));
    }
}
