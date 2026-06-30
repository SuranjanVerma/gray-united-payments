package com.grayunited.payments.service;

import com.grayunited.payments.dto.ChannelRequest;
import com.grayunited.payments.entity.IntakeLead;
import com.grayunited.payments.repository.IntakeLeadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IntakeService {

    private static final Logger log = LoggerFactory.getLogger(IntakeService.class);
    private final IntakeLeadRepository repository;

    public IntakeService(IntakeLeadRepository repository) {
        this.repository = repository;
    }

    public void processSecureChannel(ChannelRequest request) {
        IntakeLead lead = new IntakeLead();
        lead.setFullName(request.getFullName());
        lead.setWorkEmail(request.getWorkEmail());
        lead.setCompany(request.getCompany());
        lead.setContractSize(request.getContractSize());
        lead.setBrief(request.getBrief());

        repository.save(lead);
        log.info("Secure channel established and saved for: {}", request.getWorkEmail());

        // TODO (Optional later): Send a Slack notification or Email to your admin team here
    }
}