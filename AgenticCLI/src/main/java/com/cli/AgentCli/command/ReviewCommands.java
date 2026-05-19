package com.cli.AgentCli.command;


import com.cli.AgentCli.Model.PullRequest;
import com.cli.AgentCli.Model.ReviewResult;
import com.cli.AgentCli.Service.GeminiService;
import com.cli.AgentCli.Service.GitHubService;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

@Component
public class ReviewCommands {
    private GitHubService gitHubService;
    private GeminiService geminiService;

    public ReviewCommands(GitHubService gitHubService, GeminiService geminiService) {
        this.gitHubService = gitHubService;
        this.geminiService = geminiService;
    }
    @Command(name = {"ccms", "review"}, description = "Review the defined PR", group = "Review")
    public String review(@Option(longName = "pr",required = true, description = "Compare the PR differences")Integer pr){
        PullRequest pullRequest = gitHubService.fetchPr(pr);
        if (pullRequest == null){
            return "Requested PR does not exist. Check PR number and github token";
        }

        ReviewResult result = geminiService.analyzePr(pullRequest);
        if (result == null){
            return "There was an error while running the command. Try again";
        }

        return "\nCRITICAL:\n" + result.getCritical() +
                "\n\nWARNINGS:\n" + result.getWarnings() +
                "\n\nSUGGESTIONS:\n" + result.getSuggestions() +
                "\n\nRisk Score: " + result.getRiskScore() + "/10";
    }
}
