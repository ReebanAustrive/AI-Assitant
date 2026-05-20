package com.cli.AgentCli.Util;

import com.cli.AgentCli.Model.PullRequest;
import com.cli.AgentCli.Model.ReviewResult;
import com.cli.AgentCli.Service.GeminiService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RiskScoreCalculator {
    public double calculateRiskScore(ReviewResult reviewResult){
        double finalScore;
        List<String> hardcodedKeywords = List.of("hardcoded", "credentials", "secret", "password", "token");
        List<String> securityKeywords = List.of("security", "architectural", "violation", "rules");
        List<String> dependencyKeywords = List.of("dependency", "incompatible", "mismatch", "import");
        List<String> keyMismatchKeywords = List.of("key mismatch", "property", "beanCreationException");
        List<String> warningKeywords = List.of("nullPointer", "arrayindex", "classcast", "filenotfound", "arithmeticException", "numberformat");


        ArrayList<Double> criticalScores = new ArrayList<>();
        ArrayList<Double> warningScores = new ArrayList<>();
        for(String criticalKeyword : reviewResult.getCritical()){
            String lower =  criticalKeyword.toLowerCase();
            if(hardcodedKeywords.stream().anyMatch(lower::contains)){
                finalScore = 8.0;
                criticalScores.add(finalScore);
            }
            if (securityKeywords.stream().anyMatch(lower::contains)) {
                finalScore = 9.0;
                criticalScores.add(finalScore);
            }
            if (dependencyKeywords.stream().anyMatch(lower::contains)) {
                finalScore = 9.0;
                criticalScores.add(finalScore);
            }
            if (keyMismatchKeywords.stream().anyMatch(lower::contains)) {
                finalScore = 9.0;
                criticalScores.add(finalScore);
            }
        }
        for(String  warningKeyword : reviewResult.getWarnings()){
            String lower =  warningKeyword.toLowerCase();
            boolean linkedToCritical = reviewResult.getCritical()
                    .stream()
                    .anyMatch(c -> c.toLowerCase().contains(lower) || lower.contains(c.toLowerCase().substring(0, 10)));
            if(!linkedToCritical){
                if(warningKeywords.stream().anyMatch(lower::contains)){
                    finalScore = 5.0;
                    warningScores.add(finalScore);
                }
            } else  {
                if (warningKeywords.stream().anyMatch(lower::contains)){
                    finalScore = 9.0;
                    criticalScores.add(finalScore);
                }
            }
        }
        double avgCritical = criticalScores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        double avgWarning = warningScores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        double finalScores;

        if (!criticalScores.isEmpty() && !warningScores.isEmpty()) {
            finalScores = (avgCritical * 0.70) + (avgWarning * 0.30);
        } else if (!criticalScores.isEmpty()) {
            finalScores = avgCritical;
        } else if (!warningScores.isEmpty()) {
            finalScores = avgWarning;
        } else {
            finalScores = 0.0;
        }

        return Math.round(finalScores * 10.0) / 10.0;
    }
}
