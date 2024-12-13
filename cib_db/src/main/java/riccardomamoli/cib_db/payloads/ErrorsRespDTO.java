package riccardomamoli.cib_db.payloads;

import java.time.LocalDateTime;

public record ErrorsRespDTO(String message, LocalDateTime timestamp) {
}
