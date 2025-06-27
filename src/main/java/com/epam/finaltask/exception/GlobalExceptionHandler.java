package com.epam.finaltask.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidation(
            MethodArgumentNotValidException ex,
            Model model,
            HttpServletRequest request) {

        log.warn("Validation failed: {} at {}", ex.getBindingResult(), request.getRequestURI());
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (a, b) -> a
                ));

        fillCommon(model, request, 400, "Bad Request", "Validation error");
        model.addAttribute("fieldErrors", errors);
        return "error/400";
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolation(
            ConstraintViolationException ex,
            Model model,
            HttpServletRequest request) {

        log.warn("Constraint violations: {} at {}", ex.getMessage(), request.getRequestURI());
        fillCommon(model, request, 400, "Bad Request", ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleBadCredentials(
            BadCredentialsException ex,
            Model model,
            HttpServletRequest request) {

        log.warn("Authentication failure at {}: {}", request.getRequestURI(), ex.getMessage());
        fillCommon(model, request, 401, "Unauthorized", "Invalid username or password");
        return "auth/sign-in";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(
            AccessDeniedException ex,
            Model model,
            HttpServletRequest request) {

        log.warn("Access denied at {}: {}", request.getRequestURI(), ex.getMessage());
        fillCommon(model, request, 403, "Forbidden", "You do not have permission to access this page.");
        return "error/403";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(
            ResourceNotFoundException ex,
            Model model,
            HttpServletRequest request) {

        log.info("Resource not found at {}: {}", request.getRequestURI(), ex.getMessage());
        fillCommon(model, request, 404, "Not Found", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler({ org.springframework.web.HttpRequestMethodNotSupportedException.class })
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String handleMethodNotAllowed(
            Exception ex,
            Model model,
            HttpServletRequest request) {

        log.warn("Method not allowed at {}: {}", request.getRequestURI(), ex.getMessage());
        fillCommon(model, request, 405, "Method Not Allowed", ex.getMessage());
        return "error/405";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllOther(
            Exception ex,
            Model model,
            HttpServletRequest request) {

        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        fillCommon(model, request, 500, "Internal Server Error",
                "An unexpected error occurred. Please try again later.");
        return "error/500";
    }

    private void fillCommon(Model model,
                            HttpServletRequest request,
                            int status,
                            String error,
                            String message) {
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", status);
        model.addAttribute("error", error);
        model.addAttribute("message", message);
        model.addAttribute("path", request.getRequestURI());
    }
}