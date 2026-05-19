package com.cli.AgentCli.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewResult {
    private List<String> critical;
    private List<String> warnings;
    private List<String> suggestions;
    private double riskScore;
}
