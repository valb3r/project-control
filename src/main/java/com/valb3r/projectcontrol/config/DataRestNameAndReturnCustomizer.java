package com.valb3r.projectcontrol.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.GenericResponseService;
import org.springdoc.core.MethodAttributes;
import org.springdoc.core.ReturnTypeParser;
import org.springdoc.data.rest.core.ControllerType;
import org.springdoc.data.rest.core.DataRestOperationService;
import org.springdoc.data.rest.core.DataRestRepository;
import org.springdoc.data.rest.core.DataRestRequestService;
import org.springdoc.data.rest.core.DataRestResponseService;
import org.springdoc.data.rest.core.DataRestTagsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.rest.core.mapping.MethodResourceMapping;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class DataRestNameAndReturnCustomizer {

    public static final String PACKAGE_NAME = "com.valb3r.projectcontrol";

    @Bean
    DataRestOperationService dataRestOperationBuilder(DataRestRequestService dataRestRequestService, DataRestTagsService tagsBuilder,
                                                      DataRestResponseService dataRestResponseService) {
        return new DataRestOperationServiceWithCustomName(dataRestRequestService, tagsBuilder, dataRestResponseService);
    }

    @Bean
    DataRestResponseService dataRestResponseBuilder(GenericResponseService genericResponseService) {
        return new DataRestResponseServiceWithProjections(genericResponseService);
    }

    // SpringDoc does not recognize QueryResult type - making it to recognize it
    public static class DataRestResponseServiceWithProjections extends DataRestResponseService {

        private final GenericResponseService genericResponseService;

        public DataRestResponseServiceWithProjections(GenericResponseService genericResponseService) {
            super(genericResponseService);
            this.genericResponseService = genericResponseService;
        }

        @Override
        public void buildSearchResponse(Operation operation, HandlerMethod handlerMethod, OpenAPI openAPI, MethodResourceMapping methodResourceMapping, Class<?> domainType, MethodAttributes methodAttributes) {
            var componentType = methodResourceMapping.getMethod().getReturnType().getComponentType();
            if (!methodResourceMapping.getMethod().getReturnType().isAnnotationPresent(QueryResult.class)
                    && !(null != componentType && componentType.isAnnotationPresent(QueryResult.class))
            ) {
                super.buildSearchResponse(operation, handlerMethod, openAPI, methodResourceMapping, domainType, methodAttributes);
                return;
            }

            Type returnType = findSearchReturnType(handlerMethod, methodResourceMapping, domainType);
            MethodParameter methodParameterReturn = handlerMethod.getReturnType();
            ApiResponses apiResponses = new ApiResponses();
            ApiResponse apiResponse = new ApiResponse();
            Content content = genericResponseService.buildContent(openAPI.getComponents(), methodParameterReturn.getParameterAnnotations(), methodAttributes.getMethodProduces(), null, returnType);
            apiResponse.setContent(content);
            addResponse200(apiResponses, apiResponse);
            addResponse404(apiResponses);
            operation.setResponses(apiResponses);
        }

        private Type findSearchReturnType(HandlerMethod handlerMethod, MethodResourceMapping methodResourceMapping, Class<?> domainType) {
            Type returnRepoType = ReturnTypeParser.resolveType(methodResourceMapping.getMethod().getGenericReturnType(), methodResourceMapping.getMethod().getDeclaringClass());
            if (methodResourceMapping.isPagingResource()) {
                return ResolvableType.forClassWithGenerics(PagedModel.class, domainType).getType();
            }
            else if (Iterable.class.isAssignableFrom(ResolvableType.forType(returnRepoType).getRawClass())) {
                return ResolvableType.forClassWithGenerics(CollectionModel.class, domainType).getType();
            } else {
                return ResolvableType.forType(returnRepoType).getRawClass();
            }
        }

        private void addResponse200(ApiResponses apiResponses, ApiResponse apiResponse) {
            apiResponses.put(String.valueOf(HttpStatus.OK.value()), apiResponse.description(HttpStatus.OK.getReasonPhrase()));
        }

        private void addResponse404(ApiResponses apiResponses) {
            apiResponses.put(String.valueOf(HttpStatus.NOT_FOUND.value()), new ApiResponse().description(HttpStatus.NOT_FOUND.getReasonPhrase()));
        }
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
