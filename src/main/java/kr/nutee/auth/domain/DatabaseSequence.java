package kr.nutee.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "database_sequences")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatabaseSequence {

    @Id
    private String id;

    private long seq;

}
