package com.sai.junit.extension;

import com.sai.service.UserService;
import lombok.Getter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class PostProcessingExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) throws Exception {
        System.out.println("post processing extension");
        Field[] declaredFields = testInstance.getClass().getDeclaredFields();

        for (Field field: declaredFields) {
            if (field.isAnnotationPresent(Getter.class)) {
                field.set(testInstance, new UserService(null));
            }
        }

    }
}
