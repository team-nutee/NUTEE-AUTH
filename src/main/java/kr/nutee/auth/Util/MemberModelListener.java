package kr.nutee.auth.Util;

import kr.nutee.auth.Domain.Member;
import kr.nutee.auth.Service.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
public class MemberModelListener extends AbstractMongoEventListener<Member> {

    private final SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    public MemberModelListener(SequenceGeneratorService sequenceGeneratorService) {
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Member> event) {
        if (event.getSource().getId() < 1) {
            event.getSource().setId(sequenceGeneratorService.generateSequence(Member.SEQUENCE_NAME));
        }
    }
}
