package kr.nutee.auth.util;

import kr.nutee.auth.domain.Otp;
import kr.nutee.auth.service.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
public class OtpModelListener extends AbstractMongoEventListener<Otp> {

    private final SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    public OtpModelListener(SequenceGeneratorService sequenceGeneratorService) {
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Otp> event) {
        if (event.getSource().getId() < 1) {
            event.getSource().setId(sequenceGeneratorService.generateSequence(Otp.SEQUENCE_NAME));
        }
    }
}
