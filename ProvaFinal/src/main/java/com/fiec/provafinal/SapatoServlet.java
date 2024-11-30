package com.fiec.provafinal;


import com.fiec.provafinal.models.Sapato;
import com.fiec.provafinal.models.SapatoRepositorio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/sapatos")
public class SapatoServlet extends HttpServlet {

    private SapatoRepositorio sapatoRepositorio;

    public SapatoServlet() {
        // Criando o EntityManager
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("provafinalPU");
        EntityManager em = emf.createEntityManager();
        this.sapatoRepositorio = new SapatoRepositorio(em);
    }

    // Create (POST) /sapatos
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nome = request.getParameter("nome");
        String preco = request.getParameter("preco");
        String imagem = request.getParameter("imagem");
        String tamanho = request.getParameter("tamanho");
        String marca = request.getParameter("marca");

        // Criando um novo sapato
        Sapato sapato = Sapato.builder()
                .nome(nome)
                .preco(Double.parseDouble(preco))
                .imagem(imagem)
                .tamanho(Integer.parseInt(tamanho))
                .marca(marca)
                .build();

        // Salvando o sapato no banco
        sapatoRepositorio.criar(sapato);

        response.setContentType("text/html");
        response.getWriter().println("Sapato Salvo com sucesso.");
    }

    // Read (GET) /sapatos
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Sapato> sapatos = sapatoRepositorio.ler();

        response.setContentType("application/json");
        response.getWriter().println("[");
        for (int i = 0; i < sapatos.size(); i++) {
            Sapato sapato = sapatos.get(i);
            response.getWriter().println("{");
            response.getWriter().println("\"id\": \"" + sapato.getId() + "\",");
            response.getWriter().println("\"nome\": \"" + sapato.getNome() + "\",");
            response.getWriter().println("\"preco\": " + sapato.getPreco() + ",");
            response.getWriter().println("\"imagem\": \"" + sapato.getImagem() + "\",");
            response.getWriter().println("\"tamanho\": " + sapato.getTamanho() + ",");
            response.getWriter().println("\"marca\": \"" + sapato.getMarca() + "\"");
            response.getWriter().println("}" + (i < sapatos.size() - 1 ? "," : ""));
        }
        response.getWriter().println("]");
    }

    // Update (PUT) /sapatos/:id
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = getId(request);
        String nome = request.getParameter("nome");
        String preco = request.getParameter("preco");
        String imagem = request.getParameter("imagem");
        String tamanho = request.getParameter("tamanho");
        String marca = request.getParameter("marca");

        // Encontrar o sapato e atualizar
        Sapato sapato = sapatoRepositorio.lerPorId(id);
        if (sapato != null) {
            sapato.setNome(nome);
            sapato.setPreco(Double.parseDouble(preco));
            sapato.setImagem(imagem);
            sapato.setTamanho(Integer.parseInt(tamanho));
            sapato.setMarca(marca);

            sapatoRepositorio.atualiza(sapato, id);
            response.getWriter().println("Sapato atualizado com sucesso.");
        } else {
            response.getWriter().println("Sapato não encontrado.");
        }
    }

    // Delete (DELETE) /sapatos/:id
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = getId(request);

        // Deletar o sapato
        Sapato sapato = sapatoRepositorio.lerPorId(id);
        if (sapato != null) {
            sapatoRepositorio.deleta(id);
            response.getWriter().println("Sapato deletado com sucesso.");
        } else {
            response.getWriter().println("Sapato não encontrado.");
        }
    }

    // Método auxiliar para extrair o ID da URL
    private static String getId(HttpServletRequest req) {
        String path = req.getPathInfo();
        String[] paths = path.split("/");
        return paths[paths.length - 1];
    }
}
