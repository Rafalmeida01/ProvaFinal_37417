package com.fiec.provafinal;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseSingleton {

    private static FirebaseSingleton instance = null;

    private FirebaseSingleton() {
        try {
            // Obtendo o caminho do arquivo JSON de credenciais do Firebase
            String filePath = System.getenv("HOMEPATH") + "/Downloads/fiec2024-projeto.json"; // Certifique-se de que este caminho esteja correto
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(filePath)))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            System.err.println("Erro ao inicializar o Firebase: " + e.getMessage());
        }
    }

    public static FirebaseSingleton getInstance() {
        // Inicializa a instância se ainda não foi criada
        if (instance == null) {
            instance = new FirebaseSingleton();
        }
        return instance;
    }
}
