package com.cli.AgentCli.Service;


import com.cli.AgentCli.Model.ContextBundle;
import com.cli.AgentCli.Model.PullRequest;
import com.cli.AgentCli.Util.ContextStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GitHubService {
    @Autowired
    private ContextStorage contextStorage;
    private final WebClient githubWebClient;

    public GitHubService(@Qualifier("githubWebClient") WebClient githubWebClient, ContextStorage contextStorage) {
        this.githubWebClient = githubWebClient;
        this.contextStorage = contextStorage;
    }

    public PullRequest fetchPr(Integer prNumber){
       try{
           ContextBundle bundle = contextStorage.load();

           if(bundle == null){
               System.err.println("No context found. Initialize by running ccms init");
               return null;
           }
           String repoUrl =  bundle.getRepoUrl();
           String[] parts = repoUrl.replace(".git", "").split("/");
           String owner = parts[3];
           String repo = parts[4];

           Map response = githubWebClient.get()
                   .uri("/repos/{owner}/{repo}/pulls/{pr}", owner, repo, prNumber)
                   .retrieve()
                   .bodyToMono(Map.class)
                   .block();

           List<Map> files = githubWebClient.get()
                   .uri("/repos/{owner}/{repo}/pulls/{pr}/files", owner, repo, prNumber)
                   .retrieve()
                   .bodyToFlux(Map.class)
                   .collectList()
                   .block();

           List<String> patches = files.stream()
                   .map(f -> (String) f.get("patch"))
                   .filter(patch -> patch != null)
                   .collect(Collectors.toList());

           PullRequest pr = new PullRequest();
           pr.setTitle((String) response.get("title"));
           pr.setCommits((Integer) response.get("commits"));
           pr.setFilesChanged((Integer) response.get("changed_files"));
           pr.setDiff(patches);
           Map<String, Object> user = (Map<String, Object>) response.get("user");
           pr.setAuthor((String) user.get("login"));

           return pr;
       } catch (Exception e) {
           System.err.println(e.getMessage());
           return null;
       }
    }
}
