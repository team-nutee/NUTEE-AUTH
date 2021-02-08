package kr.nutee.auth.kafkaListener;

import kr.nutee.auth.dto.messageQueue.MemberMessage;
import kr.nutee.auth.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberListener {

    private final MemberService memberService;

    @KafkaListener(topics = "member-auth", containerFactory = "memberKafkaListenerContainerFactory")
    public void consumeMember(MemberMessage payload) {
        if (payload.getMethod().equals("CREATE")) {
            memberService.deleteUser(payload.getOrigin());
        }
        if (payload.getMethod().equals("UPDATE")) {
            memberService.updateUser(payload.getOrigin());
        }
    }
}
