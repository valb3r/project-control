package com.valb3r.projectcontrol.eventhandlers;

import com.valb3r.projectcontrol.config.annotation.BeforeSaveDo;
import com.valb3r.projectcontrol.config.annotation.OnSaveValidationGroup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RepositoryEventHandler
@RequiredArgsConstructor
public class EntityValidatingEventHandler {

    private final Validator validator;
    private final Map<Class, Optional<Method>> idMethodCache = new ConcurrentHashMap<>();

    @HandleBeforeCreate
    @HandleBeforeSave
    public void beforeSaveValidate(Object object) {
        doValidate(object, null);
        doValidate(object, OnSaveValidationGroup.class);
    }

    @BeforeSaveDo
    @Transactional(propagation = Propagation.MANDATORY)
    public void beforeSave(Object object) {
        validateIfNewObjectOnNullGroup(object);
        doValidate(object, OnSaveValidationGroup.class);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private void validateIfNewObjectOnNullGroup(Object object) {
        var method = idMethodCache.computeIfAbsent(object.getClass(), objClass -> {
            try {
                return Optional.of(objClass.getDeclaredMethod("getId"));
            } catch (Exception ex) {
                // NOP - no getId or inaccessible
                return Optional.empty();
            }
        });

        if (method.isEmpty()) {
            return;
        }

        // NULL id -> new Object
        if (null == method.get().invoke(object)) {
            doValidate(object, null);
        }
    }

    private void doValidate(Object object, Class<?> group) {
        Set<ConstraintViolation<Object>> errors = null == group ? validator.validate(object) : validator.validate(object, group);

        if (errors.isEmpty()) {
            return;
        }

        String error = "Validation failed: " + errors.stream().map(it -> it.getPropertyPath().toString() + ": " + it.getMessage())
                .collect(Collectors.joining(","));

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                error
        );
    }
}
