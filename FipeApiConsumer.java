import java.util.Scanner;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class FipeApiConsumer {
    private static final String BASE_URL = "https://parallelum.com.br/fipe/api/v1/carros/marcas";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Obter e listar marcas
        System.out.println("Marcas disponíveis:");
        String marcasJson = getJson(BASE_URL);
        System.out.println(marcasJson);

        System.out.print("Digite o ID da marca: ");
        String marcaId = scanner.nextLine();

        // Obter modelos
        String modelosUrl = BASE_URL + "/" + marcaId + "/modelos";
        String modelosJson = getJson(modelosUrl);

        System.out.println("Modelos disponíveis:");
        System.out.println(modelosJson);

        System.out.print("Digite o ID do modelo: ");
        String modeloId = scanner.nextLine();

        // Verificar se o modelo existe e extrair seu nome
        String modeloNome = obterNomeDoModelo(modelosJson, modeloId);
        if (modeloNome == null) {
            System.out.println("Modelo não encontrado! Verifique o ID digitado.");
            return;
        }

        System.out.println("Modelo selecionado: " + modeloNome);

        // Obter anos disponíveis para o modelo
        String anosUrl = BASE_URL + "/" + marcaId + "/modelos/" + modeloId + "/anos";
        System.out.println("Anos disponíveis:");
        String anosJson = getJson(anosUrl);
        System.out.println(anosJson);

        System.out.print("Digite o código do ano: ");
        String anoId = scanner.nextLine();

        String precoUrl = BASE_URL + "/" + marcaId + "/modelos/" + modeloId + "/anos/" + anoId;
        System.out.println("Consultando preço...");
        String precoJson = getJson(precoUrl);
        System.out.println("Preço médio: " + precoJson);
    }

    private static String obterNomeDoModelo(String modelosJson, String modeloId) {
        
        int modelosIndex = modelosJson.indexOf("\"modelos\"");
        if (modelosIndex == -1) {
            return null;
        }

        String busca = "{\"codigo\":" + modeloId + ",";
        int index = modelosJson.indexOf(busca, modelosIndex);
        if (index == -1) {
            return null; // Modelo não encontrado
        }

        int nomeIndex = modelosJson.indexOf("\"nome\":\"", index) + 8;
        int fimNome = modelosJson.indexOf("\"", nomeIndex);
        return modelosJson.substring(nomeIndex, fimNome);
    }

    private static String getJson(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new IOException("Erro ao acessar API: Código " + conn.getResponseCode());
        }

        Scanner scanner = new Scanner(conn.getInputStream());
        StringBuilder response = new StringBuilder();
        while (scanner.hasNext()) {
            response.append(scanner.nextLine());
        }
        scanner.close();
        conn.disconnect();

        return response.toString();
    }
}
