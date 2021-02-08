package kr.nutee.auth.util;

import kr.nutee.auth.dto.messageQueue.MemberMessage;
import kr.nutee.auth.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@RequiredArgsConstructor
public class KafkaSenderTemplate {

    private final KafkaTemplate<String, MemberMessage> memberKafkaTemplate;

    public void sendCreateMember(Member origin, Member change) {
        MemberMessage message = new MemberMessage("CREATE", origin, change);
        ListenableFuture<SendResult<String, MemberMessage>> future = memberKafkaTemplate
            .send("member-sns", message);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {

            }

            @Override
            public void onSuccess(SendResult<String, MemberMessage> result) {

            }
        });
    }

    public void sendUpdateMember(Member origin, Member change) {
        MemberMessage message = new MemberMessage("UPDATE", origin, change);
        ListenableFuture<SendResult<String, MemberMessage>> future = memberKafkaTemplate
            .send("member-sns", message);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {

            }

            @Override
            public void onSuccess(SendResult<String, MemberMessage> result) {

            }
        });
    }

}
