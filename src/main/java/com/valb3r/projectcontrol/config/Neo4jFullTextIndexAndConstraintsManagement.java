package com.valb3r.projectcontrol.config;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.ogm.session.Session;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class Neo4jFullTextIndexAndConstraintsManagement {

    private final TransactionOperations operations;
    private final Session session;
    private final Schema neo4jSchema;

    @PostConstruct
    public void createIndexesAndConstraints() {
        // TODO: Schema management solution
        neo4jSchema.getIndexes().forEach(this::executeIndexCreationStatement);
        neo4jSchema.getConstraints().forEach(this::executeConstraintCreationStatement);
    }

    private void executeIndexCreationStatement(FullTextIndexType type, String statement) {
        String targetStatement = statement.replace("%", type.getName());
        executeStatement(targetStatement);
    }

    private void executeConstraintCreationStatement(String statement) {
        executeStatement(statement);
    }

    private void executeStatement(String statement) {
        try {
            operations.execute(callback -> {
                session.query(statement, Map.of());
                return null;
            });
        } catch (Exception ex) {
            String message = ex.getMessage();
            if (message.contains("An equivalent index already exists") || message.contains("An equivalent constraint already exists") || message.contains("Constraint already exists")) {
                log.info("{} seem to be already executed", statement);
                return;
            }

            throw new RuntimeException(ex);
        }
    }

    @Data
    @Validated
    @Configuration
    @ConfigurationProperties("pcontrol.neo4j")
    static class Schema {

        @Valid
        @NotNull
        private Map<@NotNull FullTextIndexType, @NotBlank String> indexes;

        @Valid
        @NotNull
        private List<@NotBlank String> constraints;
    }

    public static final String USER_IDX = "USER_IDX";

    @Getter
    @RequiredArgsConstructor
    public enum FullTextIndexType {
        USER(USER_IDX);

        private final String name;
    }
}
