package com.justintime.jit.validators;

import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.exception.InvalidPayloadException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

@Aspect
@Component
public class ValidationAspect {

    @Autowired
    protected ValidationDispatcher validationDispatcher;

    @Around("@annotation(ValidateInput)")
    public Object validateBeforeController(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        Object dto = null;
        Set<String> fieldsToValidate = null;
        String restaurantCode = null;

        for (Object arg : args) {
            if (arg instanceof PatchRequest<?> patchRequest) {
                dto = patchRequest.getDto();
                fieldsToValidate = patchRequest.getPropertiesToBeUpdated();
            } else if (arg instanceof String str && str.matches("^[A-Z0-9_]+$")) {
                restaurantCode = str;
            } else if (restaurantCode == null) {
                restaurantCode = extractRestaurantCodeFromObject(arg);
            } else if (isCustomDTO(arg)) {
                dto = arg;
            }
        }

        if (dto == null) {
            throw new InvalidPayloadException("Invalid payload.");
        }

        validationDispatcher.process(dto, fieldsToValidate, restaurantCode);

        return joinPoint.proceed();
    }

    private String extractRestaurantCodeFromObject(Object arg) {
        Method method;
        try {
            method = arg.getClass().getMethod("getRestaurantCode");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        Object result;
        try {
            result = method.invoke(arg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return (result instanceof String) ? (String) result : null;

    }

    private boolean isCustomDTO(Object arg) {
        return !(arg instanceof String || arg instanceof Number || arg instanceof Set || arg instanceof Map || arg.getClass().isPrimitive());
    }
}
