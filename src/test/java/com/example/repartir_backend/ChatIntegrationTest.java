package com.example.repartir_backend;

import com.example.repartir_backend.dto.ChatMessageDto;
import com.example.repartir_backend.dto.ChatMessageResponseDto;
import com.example.repartir_backend.security.JwtServices;
import com.example.repartir_backend.services.UserDetailsImplService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test d'intégration pour la fonctionnalité de chat via WebSocket.
 * Ce test démarre l'application complète, génère un token JWT,
 * et simule un client STOMP pour envoyer et recevoir des messages.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtServices jwtServices;

    @Autowired
    private UserDetailsImplService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private WebSocketStompClient stompClient;

    @BeforeEach
    public void setup() {
        // Nous configurons un client WebSocket simple, sans SockJS,
        // car c'est la configuration actuelle de notre serveur.
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper); // Utiliser l'ObjectMapper configuré par Spring, qui connaît LocalDateTime
        this.stompClient.setMessageConverter(converter);
    }

    @Test
    public void testSendMessageAndReceiveSuccessfully() throws Exception {
        // --- PRÉPARATION ---
        // 1. Générer un token JWT pour un utilisateur de test.
        // NOTE: Assurez-vous que l'utilisateur 'jeune@example.com' existe dans votre base de données de test,
        // et qu'un mentoring avec l'ID 1 existe également.
        final UserDetails userDetails = userDetailsService.loadUserByUsername("celine.durand@email.com");
        final String token = jwtServices.genererToken(userDetails);

        // 2. Préparer les headers pour la connexion STOMP. C'est ici que nous passons le token.
        final StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);

        // 3. Utiliser un CountDownLatch pour gérer l'asynchronisme.
        // Le test attendra jusqu'à ce que le latch soit décrémenté à zéro (quand le message est reçu).
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        
        final AtomicReference<ChatMessageResponseDto> receivedMessage = new AtomicReference<>();

        // --- EXÉCUTION ---
        // 4. Définir le gestionnaire de session STOMP qui dicte quoi faire à chaque étape.
        StompSessionHandlerAdapter sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
                System.out.println(">>> Test Client: Connecté à la session " + session.getSessionId());

                // 5. Une fois connecté, s'abonner au topic du chat.
                session.subscribe("/topic/chat/1", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return ChatMessageResponseDto.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        System.out.println(">>> Test Client: Message reçu: " + payload);
                        // Stocker le message reçu et décrémenter le latch.
                        receivedMessage.set((ChatMessageResponseDto) payload);
                        latch.countDown();
                    }
                });

                System.out.println(">>> Test Client: Abonné à /topic/chat/1");

                // 6. Envoyer le message de test au serveur.
                ChatMessageDto testMessage = new ChatMessageDto("Bonjour Amadou !");
                session.send("/app/chat/1", testMessage);
                System.out.println(">>> Test Client: Message envoyé à /app/chat/1");
            }

            @Override
            public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
                System.err.println(">>> Test Client: Erreur (handleException)");
                failure.set(ex);
                latch.countDown();
            }

            @Override
            public void handleTransportError(StompSession session, Throwable ex) {
                System.err.println(">>> Test Client: Erreur de transport");
                failure.set(ex);
                latch.countDown();
            }
        };

        // 7. Exécuter la connexion au serveur WebSocket.
        String url = "ws://localhost:" + port + "/ws";
        this.stompClient.connect(url, new WebSocketHttpHeaders(), connectHeaders, sessionHandler);

        // --- VÉRIFICATION ---
        // 8. Attendre que le message soit reçu, avec un timeout de 10 secondes.
        if (!latch.await(10, TimeUnit.SECONDS)) {
            fail("Le message n'a pas été reçu dans le temps imparti.");
        }

        // 9. Vérifier qu'aucune erreur de transport ou de protocole n'a eu lieu.
        if (failure.get() != null) {
            throw new AssertionError("Le test a échoué avec une exception.", failure.get());
        }

        // 10. Vérifier que le message reçu est correct.
        assertNotNull(receivedMessage.get(), "Le message reçu ne doit pas être nul.");
        assertEquals("Bonjour Amadou !", receivedMessage.get().getContent());
    }
}
