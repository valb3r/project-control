package com.valb3r.projectcontrol.interceptors;

import com.valb3r.projectcontrol.config.OpenInViewSpringDataInterceptor;
import com.valb3r.projectcontrol.config.annotation.AfterSaveDo;
import com.valb3r.projectcontrol.config.annotation.BeforeSaveDo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.session.event.Event;
import org.neo4j.ogm.session.event.EventListenerAdapter;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The @HandleBeforeCreate /  @HandleBeforeSave do NOT happen in same transaction as `save` method.
 * See https://stackoverflow.com/questions/25717127/handle-spring-data-rest-application-events-within-the-transaction/30713264#30713264
 * for details. See i.e. {@code org.springframework.data.rest.webmvc.RepositoryEntityController#postCollectionResource} for
 * details.
 *
 * Adding custom annotations and event handling that targets {@link org.springframework.data.repository.support.RepositoryInvoker}
 * to catch pre-create, pre-save events in same transaction.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SpringDataRestSaveCreateInterceptor extends EventListenerAdapter {

    private final Map<Class<?>, List<BeanAndMethod>> preSave = new HashMap<>();
    private final Map<Class<?>, List<BeanAndMethod>> postSave = new HashMap<>();
    private ApplicationContext context;

    public SpringDataRestSaveCreateInterceptor(SessionFactory factory) {
        factory.register(this);
    }

    @EventListener(classes = { ContextRefreshedEvent.class })
    public void onRefresh(ContextRefreshedEvent ev) throws BeansException {
        var ctx = ev.getApplicationContext();
        for (String name: ctx.getBeanDefinitionNames()) {
            Method[] methods = AopUtils.getTargetClass(ctx.getBean(name)).getDeclaredMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(BeforeSaveDo.class) && !method.isAnnotationPresent(AfterSaveDo.class)) {
                    continue;
                }

                if (method.getParameterCount() != 1) {
                    throw new IllegalStateException(String.format("Method %s#%s should have exactly 1 argument", method.getDeclaringClass().getCanonicalName(), method.getName()));
                }

                if (method.isAnnotationPresent(BeforeSaveDo.class)) {
                    Class desiredObject = method.getParameterTypes()[0];
                    preSave.computeIfAbsent(desiredObject, id -> new ArrayList<>()).add(new BeanAndMethod(ctx.getBean(name), method));
                }

                if (method.isAnnotationPresent(AfterSaveDo.class)) {
                    Class desiredObject = method.getParameterTypes()[0];
                    postSave.computeIfAbsent(desiredObject, id -> new ArrayList<>()).add(new BeanAndMethod(ctx.getBean(name), method));
                }
            }
        }

        context = ctx;
    }

    @Override
    @SneakyThrows
    public void onPreSave(Event event) {
        var targets = new ArrayList<BeanAndMethod>();
        for (var entries : preSave.entrySet()) {
            if (!entries.getKey().isInstance(event.getObject())) {
                continue;
            }
            targets.addAll(entries.getValue());
        }

        if (targets.isEmpty()) {
            return;
        }

        context.getBean(TransactionalPreSaveHandler.class)
                .doHandlePreSaveTransactionally(event, targets);
    }

    @Override
    @SneakyThrows
    public void onPostSave(Event event) {
        var targets = new ArrayList<BeanAndMethod>();
        for (var entries : postSave.entrySet()) {
            if (!entries.getKey().isInstance(event.getObject())) {
                continue;
            }
            targets.addAll(entries.getValue());
        }

        if (targets.isEmpty()) {
            return;
        }

        context.getBean(TransactionalPostSaveHandler.class)
                .doHandlePostSaveTransactionally(event, targets);
    }

    @Service
    public static class TransactionalPreSaveHandler {

        /**
         * Due to {@link OpenInViewSpringDataInterceptor} there should be a Transaction opened.
         */
        @SneakyThrows
        @Transactional(propagation = Propagation.MANDATORY)
        public void doHandlePreSaveTransactionally(Event event, List<BeanAndMethod> targets) {
            for (var beanAndMethod : targets) {
                try {
                    beanAndMethod.doInvoke(event.getObject());
                } catch (InvocationTargetException wrapper) {
                    throw wrapper.getTargetException();
                }
            }
        }
    }

    @Service
    public static class TransactionalPostSaveHandler {

        /**
         * Due to {@link OpenInViewSpringDataInterceptor} there should be a Transaction opened.
         */
        @SneakyThrows
        @Transactional(propagation = Propagation.MANDATORY)
        public void doHandlePostSaveTransactionally(Event event, List<BeanAndMethod> targets) {
            for (var beanAndMethod : targets) {
                try {
                    beanAndMethod.doInvoke(event.getObject());
                } catch (InvocationTargetException wrapper) {
                    throw wrapper.getTargetException();
                }
            }
        }
    }
}