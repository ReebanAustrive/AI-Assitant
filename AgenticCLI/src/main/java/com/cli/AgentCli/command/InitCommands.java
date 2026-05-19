package com.cli.AgentCli.command;


import com.cli.AgentCli.Service.ContextService;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

@Component
public class InitCommands {
    private ContextService contextService;

    public InitCommands(ContextService contextService) {
        this.contextService = contextService;
    }
    @Command(name = {"ccms", "init"}, description = "Initialize the context window", group = "initialize")
    public String initRepo(@Option(longName = "repo", required = true, description = "Gets the repo URL")String repoURL,
                         @Option(longName = "arch", required = true, description = "Path for architecture document") String archPath,
                         @Option(longName = "rule", required = false, description = "Sets the initial rules into context window") String rulePath) {
            return contextService.initContext(repoURL, archPath, rulePath);
    }
}
