package za.co.kpolit.document_service.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class OpenAiWrapper {

    private OpenAiService openAiService;
    @Value("${openai.api.key}")
    private String openaiApiKey;
    final private  int timeout = 120;

    @PostConstruct
    public void init(){
        if (openaiApiKey != null && !openaiApiKey.isEmpty()) {
            openAiService = new OpenAiService(openaiApiKey, Duration.ofSeconds(timeout));
        } else {
            System.out.println("WARNING: OPENAI_API_KEY not set");
        }
    }
    public String summarize(String text) {
        String prompt = "Please provide a concise summary of the following text (about 150-250 words):\n\n" + text;
        return callChat(prompt, 400);
    }

    public String generateQuiz(String text, int count) {
        String prompt = "Generate " + count + " multiple-choice questions (JSON array) from the text. Each item should be: {\"question\": \"...\", \"options\": [\"A\",\"B\",\"C\",\"D\"], \"answer\": \"A\"}. Only return JSON array. Text:\n\n" + text;
        return callChat(prompt, 700);
    }

    public String generateTest(String text, int count) {
        String prompt = "Generate " + count + " test questions (mixed multiple choice and true/false) in JSON format. Each item: {\"type\":\"mcq\"|\"tf\",\"question\":\"...\",\"options\":[...],\"answer\":\"...\"}. Only return JSON array. Text:\n\n" + text;
        return callChat(prompt, 1200);
    }

    private String callChat(String prompt, int maxTokens) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4o-mini")
                .messages(List.of(new ChatMessage("user", prompt)))
                .maxTokens(maxTokens)
                .temperature(0.2)
                .build();
        var resp = openAiService.createChatCompletion(request);
        return resp.getChoices().get(0).getMessage().getContent().trim();
    }
}
