package com.bpoconnect.service;

import org.springframework.stereotype.Service;
import com.bpoconnect.model.QualityEvaluation;
import java.util.List;

@Service public class QualityService { 
    public void submitEvaluation(){} 
    public void flagForCoaching(){} 
    public int getAgentQAScore(){return 0;} 
    public void notifyAgentAndSupervisor(){}
    public List<QualityEvaluation> getAgentEvaluations(String agentId){return null;}
}
