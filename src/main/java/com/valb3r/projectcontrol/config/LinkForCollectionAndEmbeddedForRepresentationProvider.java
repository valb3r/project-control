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
import org.springframework.hateoas.RepresentationModel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Improves _link support by SpringDoc - replaces entities without _links in collection responses to EntityModel that
 * includes _link. Also for RepresentationModel returns correct entity in _embedded
 */
@Configuration
public class LinkForCollectionAndEmbeddedForRepresentationProvider {

    @Bean
    GenericResponseService responseBuilderWithLinks(OperationService operationService, List<ReturnTypeParser> returnTypeParsers, SpringDocConfigProperties springDocConfigProperties, PropertyResolverUtils propertyResolverUtils) {
        return new GenericResponseServiceWithHalLinksInCollectionAndEmbeddedInRepresentationModel(operationService, returnTypeParsers, springDocConfigProperties, propertyResolverUtils);
    }

    /**
     * Adds _link to collection models schema
     */
    public static class GenericResponseServiceWithHalLinksInCollectionAndEmbeddedInRepresentationModel extends GenericResponseService {

        public GenericResponseServiceWithHalLinksInCollectionAndEmbeddedInRepresentationModel(OperationService operationService, List<ReturnTypeParser> returnTypeParsers, SpringDocConfigProperties springDocConfigProperties, PropertyResolverUtils propertyResolverUtils) {
            super(operationService, returnTypeParsers, springDocConfigProperties, propertyResolverUtils);
        }

        @Override
        public Content buildContent(Components components, Annotation[] annotations, String[] methodProduces, JsonView jsonView, Type returnType) {
            var resolvable = ResolvableType.forType(returnType);
            var targetType = returnType;
            if (null != resolvable.getRawClass() && CollectionModel.class.isAssignableFrom(resolvable.getRawClass())) {
                targetType = ResolvableType.forClassWithGenerics(resolvable.getRawClass(), ResolvableType.forClassWithGenerics(EntityModel.class, resolvable.getGenerics())).getType();
            }
            if (isRepresentationModel(resolvable)) {
                targetType = ResolvableType.forClassWithGenerics(resolvable.getRawClass(),ResolvableType.forClassWithGenerics(EntityModel.class, resolvable.getNested(3))).getType();
            }

            return super.buildContent(components, annotations, methodProduces, jsonView, targetType);
        }

        private boolean isRepresentationModel(ResolvableType resolvableType) {
            var clz = rawClassOfRepresentationModel(resolvableType);
            return null != clz &&  RepresentationModel.class.isAssignableFrom(clz);
        }

        private Class<?> rawClassOfRepresentationModel(ResolvableType resolvableType) {
            return resolvableType.getNested(2).getRawClass();
        }
    }
}
