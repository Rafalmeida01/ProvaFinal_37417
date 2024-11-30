package com.fiec.provafinal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Inicializando o cliente SQS
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        String nomeDaFila = "Fiec2024";

        // Loop contínuo para verificar a fila a cada 30 segundos
        while (true) {
            Thread.sleep(30000); // Espera de 30 segundos
            try {
                // Recebe uma mensagem da fila
                ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                        .queueUrl(nomeDaFila)
                        .maxNumberOfMessages(1)
                        .build();

                List<software.amazon.awssdk.services.sqs.model.Message> responses = sqsClient.receiveMessage(receiveMessageRequest).messages();
                for (software.amazon.awssdk.services.sqs.model.Message m : responses) {

                    System.out.println("Mensagem recebida: " + m.body());

                    // Processando o JSON da mensagem
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(m.body());
                    String token = jsonNode.get("token").asText(); // Extraindo o token da mensagem

                    // Envia a mensagem via Firebase
                    sendMessage(token);

                    // Deleta a mensagem da fila após o processamento
                    DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                            .queueUrl(nomeDaFila)
                            .receiptHandle(m.receiptHandle())
                            .build();
                    sqsClient.deleteMessage(deleteMessageRequest);
                    System.out.println("Mensagem deletada da fila.");
                }

            } catch (SqsException e) {
                // Tratamento de erros do SQS
                sqsClient.close();
                System.err.println("Erro no SQS: " + e.awsErrorDetails().errorMessage());
                // Reinicia o cliente SQS para tentativas futuras
                sqsClient = SqsClient.builder().region(Region.US_EAST_1).build();
                System.exit(1);

            } catch (JsonProcessingException e) {
                // Erro no processamento do JSON
                System.err.println("Erro ao processar JSON: " + e.getMessage());
            }
        }
    }

    private static void sendMessage(String token) {
        System.out.println("Enviando notificação para o token: " + token);

        // Inicializa a instância do Firebase
        FirebaseSingleton.getInstance();

        // Criação da mensagem de notificação
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle("Notificação da Fila")
                        .setBody("Fiec2024 - Enviando notificação para você")
                        .build())
                .build();

        try {
            // Envia a mensagem via Firebase
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Mensagem enviada com sucesso: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("Erro ao enviar a mensagem para o Firebase: " + e.getMessage());
            throw new RuntimeException("Erro ao enviar mensagem para o Firebase", e);
        }
    }
}
