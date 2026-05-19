package com.cli.AgentCli.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest {
    private int prNumber;
    private String title;
    private String author;
    private List<String> diff;
    private int filesChanged;
    private int commits;
}
