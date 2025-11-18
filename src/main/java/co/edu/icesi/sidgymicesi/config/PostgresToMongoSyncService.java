package co.edu.icesi.sidgymicesi.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.PostConstruct;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
@Profile("postgres & mongo")
public class PostgresToMongoSyncService {

    private static final Logger log = LoggerFactory.getLogger(PostgresToMongoSyncService.class);

    private static final String CHANNEL_NAME = "user_updates";

    private final DataSource dataSource;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PostgresToMongoSyncService(DataSource dataSource, MongoTemplate mongoTemplate) {
        this.dataSource = dataSource;
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void startListener() {
        Thread listenerThread = new Thread(this::listenLoop, "pg-user-updates-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();

        log.info("[PG→Mongo Sync] Listener thread inicializado (canal: {}).", CHANNEL_NAME);
    }

    private void listenLoop() {
        // Bucle externo: si algo falla, reintenta con backoff.
        while (true) {
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {

                log.info("[PG→Mongo Sync] Conectado a Postgres. Ejecutando LISTEN {}...", CHANNEL_NAME);
                statement.execute("LISTEN " + CHANNEL_NAME);

                PGConnection pgConnection = connection.unwrap(PGConnection.class);

                // Bucle interno: mientras la conexión esté viva, seguimos leyendo notificaciones
                while (!Thread.currentThread().isInterrupted()) {
                    // Esperamos un poco antes de consultar notificaciones
                    Thread.sleep(5000L);

                    PGNotification[] notifications = pgConnection.getNotifications();
                    if (notifications == null || notifications.length == 0) {
                        continue;
                    }

                    for (PGNotification notification : notifications) {
                        String payload = notification.getParameter();
                        log.debug("[PG→Mongo Sync] Notificación recibida: {}", payload);
                        handleNotification(payload);
                    }
                }
            } catch (InterruptedException ie) {
                // Si el thread fue interrumpido, salimos limpiamente
                Thread.currentThread().interrupt();
                log.warn("[PG→Mongo Sync] Listener interrumpido, terminando hilo.");
                return;
            } catch (Exception ex) {
                log.error("[PG→Mongo Sync] Error en el listener. Reintentando en 10 segundos...", ex);
                try {
                    Thread.sleep(10_000L);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private void handleNotification(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);

            String operation = root.path("operation").asText(null);
            if (!"UPDATE_USERNAME".equals(operation)) {
                log.debug("[PG→Mongo Sync] Operación '{}' ignorada.", operation);
                return;
            }

            String oldUsername = root.path("old_username").asText(null);
            String newUsername = root.path("new_username").asText(null);

            if (oldUsername == null || newUsername == null) {
                log.warn("[PG→Mongo Sync] Payload inválido (faltan campos): {}", payload);
                return;
            }

            if (oldUsername.equals(newUsername)) {
                log.debug("[PG→Mongo Sync] Username sin cambios ({}). Nada que sincronizar.", oldUsername);
                return;
            }

            log.info("[PG→Mongo Sync] Detectado cambio de username: {} → {}", oldUsername, newUsername);
            updateMongoDocuments(oldUsername, newUsername);

        } catch (Exception e) {
            log.error("[PG→Mongo Sync] Error procesando payload: {}", payload, e);
        }
    }

    private void updateMongoDocuments(String oldUsername, String newUsername) {
        // 1) user_routines.ownerUsername
        Query routinesQuery = Query.query(Criteria.where("ownerUsername").is(oldUsername));
        Update routinesUpdate = new Update().set("ownerUsername", newUsername);
        UpdateResult routinesResult =
                mongoTemplate.updateMulti(routinesQuery, routinesUpdate, "user_routines");

        // 2) progress_logs.ownerUsername
        Query logsQuery = Query.query(Criteria.where("ownerUsername").is(oldUsername));
        Update logsUpdate = new Update().set("ownerUsername", newUsername);
        UpdateResult logsResult =
                mongoTemplate.updateMulti(logsQuery, logsUpdate, "progress_logs");

        // 3) trainer_assignments.user_username
        Query assignmentsQuery = Query.query(Criteria.where("user_username").is(oldUsername));
        Update assignmentsUpdate = new Update().set("user_username", newUsername);
        UpdateResult assignmentsResult =
                mongoTemplate.updateMulti(assignmentsQuery, assignmentsUpdate, "trainer_assignments");

        log.info(
                "[PG→Mongo Sync] Mongo actualizado para {} → {}. " +
                        "user_routines: matched={}, modified={}; " +
                        "progress_logs: matched={}, modified={}; " +
                        "trainer_assignments: matched={}, modified={};",
                oldUsername, newUsername,
                routinesResult.getMatchedCount(), routinesResult.getModifiedCount(),
                logsResult.getMatchedCount(), logsResult.getModifiedCount(),
                assignmentsResult.getMatchedCount(), assignmentsResult.getModifiedCount()
        );
    }
}
