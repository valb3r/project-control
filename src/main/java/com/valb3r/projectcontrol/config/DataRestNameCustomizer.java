package com.valb3r.projectcontrol.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import org.springdoc.core.MethodAttributes;
import org.springdoc.data.rest.core.ControllerType;
import org.springdoc.data.rest.core.DataRestOperationService;
import org.springdoc.data.rest.core.DataRestRepository;
import org.springdoc.data.rest.core.DataRestRequestService;
import org.springdoc.data.rest.core.DataRestResponseService;
import org.springdoc.data.rest.core.DataRestTagsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.mapping.MethodResourceMapping;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class DataRestNameCustomizer {

    public static final String PACKAGE_NAME = "com.valb3r.projectcontrol";

    @Bean
    DataRestOperationService dataRestOperationBuilder(DataRestRequestService dataRestRequestService, DataRestTagsService tagsBuilder,
                                                      DataRestResponseService dataRestResponseService) {
        return new DataRestOperationServiceWithCustomName(dataRestRequestService, tagsBuilder, dataRestResponseService);
    }

    public static class DataRestOperationServiceWithCustomName extends DataRestOperationService {
        public DataRestOperationServiceWithCustomName(DataRestRequestService dataRestRequestService, DataRestTagsService tagsBuilder, DataRestResponseService dataRestResponseService) {
            super(dataRestRequestService, tagsBuilder, dataRestResponseService);
        }

        @Override
        public Operation buildOperation(HandlerMethod handlerMethod, DataRestRepository dataRestRepository,
                                        OpenAPI openAPI, RequestMethod requestMethod, String operationPath,
                                        MethodAttributes methodAttributes, ResourceMetadata resourceMetadata,
                                        MethodResourceMapping methodResourceMapping, ControllerType controllerType
        ) {
            var oper = super.buildOperation(handlerMethod, dataRestRepository, openAPI, requestMethod, operationPath, methodAttributes, resourceMetadata, methodResourceMapping, controllerType);
            if (null != methodResourceMapping && methodResourceMapping.getMethod().getDeclaringClass().getPackage().getName().contains(PACKAGE_NAME)) {
                // Note - uniqueness is not guaranteed
                oper.setOperationId(methodResourceMapping.getMethod().getName() + "_" +
                        Arrays.stream(methodResourceMapping.getMethod().getParameterTypes()).map(it -> it.getSimpleName().substring(0, 1)).collect(Collectors.joining())
                );
            }

            return oper;
        }
    }
}
