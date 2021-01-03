package com.valb3r.projectcontrol.config;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Content;
import org.springdoc.core.GenericResponseService;
import org.springdoc.core.OperationService;
import org.springdoc.core.PropertyResolverUtils;
import org.springdoc.core.ReturnTypeParser;
import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

@Configuration
public class LinkForCollectionProvider {

    @Bean
    GenericResponseService responseBuilderWithLinks(OperationService operationService, List<ReturnTypeParser> returnTypeParsers, SpringDocConfigProperties springDocConfigProperties, PropertyResolverUtils propertyResolverUtils) {
        return new GenericResponseServiceWithHalLinksInCollection(operationService, returnTypeParsers, springDocConfigProperties, propertyResolverUtils);
    }

    /**
     * Adds _link to collection models schema
     */
    public static class GenericResponseServiceWithHalLinksInCollection extends GenericResponseService {

        public GenericResponseServiceWithHalLinksInCollection(OperationService operationService, List<ReturnTypeParser> returnTypeParsers, SpringDocConfigProperties springDocConfigProperties, PropertyResolverUtils propertyResolverUtils) {
            super(operationService, returnTypeParsers, springDocConfigProperties, propertyResolverUtils);
        }

        @Override
        public Content buildContent(Components components, Annotation[] annotations, String[] methodProduces, JsonView jsonView, Type returnType) {
            var resolvable = ResolvableType.forType(returnType);
            var targetType = returnType;
            if (CollectionModel.class.isAssignableFrom(resolvable.getRawClass())) {
                targetType = ResolvableType.forClassWithGenerics(resolvable.getRawClass(), ResolvableType.forClassWithGenerics(EntityModel.class, resolvable.getGenerics())).getType();
            }
            return super.buildContent(components, annotations, methodProduces, jsonView, targetType);
        }
    }
}
