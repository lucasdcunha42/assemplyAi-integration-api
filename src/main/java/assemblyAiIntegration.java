import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class assemblyAiIntegration {
    public static void main(String[] args) throws Exception {
        Transcript transcript = new Transcript();
        transcript.setAudio_url("http://www.biblioteca.presidencia.gov.br/presidencia/ex-presidentes/luiz-inacio-lula-da-silva/audios/2010-audios-lula/27-10-2010-palavras-do-presidente-da-republica-luiz-inacio-lula-da-silva-apos-receber-a-noticia-do-falecimento-do-ex-presidente-da-argentina-nestor-kirchner-itajai-sc-53s/@@download/file/27-10-2010%20-%20Palavras%20do%20Presidente%20da%20Rep%C3%BAblica,%20Luiz%20In%C3%A1cio%20Lula%20da%20Silva,%20ap%C3%B3s%20receber%20a%20not%C3%ADcia%20do%20falecimento%20do%20ex-presidente%20da%20Argentina,%20N%C3%A9stor%20Kirchner%20-%20Itaja%C3%AD-SC%20(53s).mp3");
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(transcript);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                .header("Authorization", Constants.API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println(postResponse.body());

        transcript = gson.fromJson(postResponse.body(),Transcript.class);

        System.out.println(transcript.getId());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript/" + transcript.getId()))
                .header("Authorization", Constants.API_KEY)
                .build();

        while (true) {
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            transcript = gson.fromJson(getResponse.body(), Transcript.class);
            System.out.println(transcript.getStatus());

            if ("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus())){
                break;
            }
            Thread.sleep(1000);
        }
        System.out.println("Transcript Completed");
        System.out.println(transcript.getText());
        System.out.println(transcript.getError());
    }
}
