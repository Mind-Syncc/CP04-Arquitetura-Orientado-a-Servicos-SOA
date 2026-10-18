package br.com.fiap3espv.AutoEscola3ESPV.utils;

import br.com.fiap3espv.AutoEscola3ESPV.domain.endereco.DadosEndereco;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BuscaEnderecoViaCep {

    public static DadosEndereco buscarCep(String cep, String numero) {
        try {
            String url = "https://viacep.com.br/ws/" + cep + "/json/";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erro na consulta ao ViaCEP");
            }

            ObjectMapper mapper = new ObjectMapper();

            JsonNode json = mapper.readTree(response.body());

            if (json.has("erro") && json.get("erro").asBoolean()) {
                throw new RuntimeException("CEP não encontrado: " + cep);
            }

            DadosEndereco enderecoApi =
                    mapper.treeToValue(json, DadosEndereco.class);

            return new DadosEndereco(
                    enderecoApi.logradouro(),
                    numero,
                    enderecoApi.complemento(),
                    enderecoApi.bairro(),
                    enderecoApi.cidade(),
                    enderecoApi.uf(),
                    enderecoApi.cep()
            );


        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Erro ao consultar o ViaCEP para o CEP: " + cep, e
            );
        }
    }
}
