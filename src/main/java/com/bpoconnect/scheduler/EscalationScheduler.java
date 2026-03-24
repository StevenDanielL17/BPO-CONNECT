package com.bpoconnect.scheduler;

import org.springframework.stereotype.Component; import org.springframework.scheduling.annotation.Scheduled;
@Component public class EscalationScheduler { @Scheduled(fixedRate=300000) public void runAutoEscalation(){} }
