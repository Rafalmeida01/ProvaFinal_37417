package com.fiec.provafinal;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 10 * 5 // 50 MB
)
@WebServlet("/uploads")
public class UploadServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Part filePart = request.getPart("arquivo");
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String extensao = fileName.split("\\.")[1];
        String bucket = "alunofiecbucket";
        String temp = System.getenv("TEMP");
        String uuid = UUID.randomUUID().toString();
        String nomeArquivoNoBucket = "uploads/" + "SEURM" + uuid + "." + extensao; // Atualizando a chave para incluir a pasta

        File file = new File(temp + "/" + nomeArquivoNoBucket);
        filePart.write(file.getAbsolutePath());

        S3Client s3 = S3Client.builder()
                .region(Region.US_EAST_1) // Substitua pela região do seu bucket
                .build();

        Map<String, String> metadata = new HashMap<>();
        metadata.put("author", "Fiec");

        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucket)
                .key("37417/" + nomeArquivoNoBucket)
                .metadata(metadata)
                .build();

        s3.putObject(putOb, RequestBody.fromFile(new File(file.getAbsolutePath())));
        System.out.println("Successfully placed " + nomeArquivoNoBucket + " into bucket " + bucket);

        file.delete();

        // Exibindo uma mensagem de sucesso
        response.setContentType("text/html");
        response.getWriter().println("<h1>Arquivo Enviado com sucesso!</h1>");
    }
}
