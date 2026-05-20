package com.cli.AgentCli.Service;

import com.cli.AgentCli.Model.ContextBundle;
import com.cli.AgentCli.Model.PullRequest;
import com.cli.AgentCli.Model.ReviewResult;
import com.cli.AgentCli.Util.ContextStorage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    private ContextStorage contextStorage;
    private final WebClient geminiWebClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${gemini.api.key}")
    private String geminiApiKey;
    private GeminiService(ContextStorage contextStorage, @Qualifier("geminiWebClient")  WebClient geminiWebClient) {
        this.contextStorage = contextStorage;
        this.geminiWebClient = geminiWebClient;
    }

    public ReviewResult analyzePr(PullRequest pr) {
        try{
            ContextBundle bundle = contextStorage.load();

            if(bundle == null){
                System.err.println("No context found. Re-Initialize by running ccms init");
                return null;
            }
            String systemPrompt = "You are a code reviewer for this project. \n Architecture:"+ bundle.getArchitectureContent()+"\n"+"Rules: \n"+bundle.getRules().toString()+"\n"+ "Only evaluate against this context. Return json only.";
            String userPrompt = "Review this PR diff:\n" + String.join("\n", pr.getDiff()) +
                    "\n\nReturn ONLY a valid JSON object with exactly these fields:" +
                    "\n{" +
                    "\n  \"critical\": [\"issue 1\", \"issue 2\"]," +
                    "\n  \"warnings\": [\"warning 1\"]," +
                    "\n  \"suggestions\": [\"suggestion 1\"]," +
                    "\n  \"riskScore\": 0.0" +
                    "\n}" +
                    "\nDo NOT wrap in markdown. Do NOT use backticks. Return raw JSON only.";
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(Map.of("text", systemPrompt + "\n" + userPrompt))
                    ))
            );

            Map response = geminiWebClient.post()
                    .uri("/v1beta/models/gemini-3-flash-preview:generateContent?key=" + geminiApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            List<Map> candidates = (List<Map>) response.get("candidates");
            Map content = (Map) candidates.get(0).get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            String text = (String) parts.get(0).get("text");
            ReviewResult reviewResult = objectMapper.readValue(text, ReviewResult.class);

            return reviewResult;
        } catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }
}
