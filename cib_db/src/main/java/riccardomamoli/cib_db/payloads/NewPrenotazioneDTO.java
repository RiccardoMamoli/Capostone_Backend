package riccardomamoli.cib_db.payloads;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record NewPrenotazioneDTO(

        @NotNull(message = "L'ID dell'utente è obbligatorio!")
        Long utenteId,

        @NotNull(message = "L'ID dell'oggetto è obbligatorio!")
        Long oggettoId,

        @NotNull(message = "La data di prenotazione è obbligatoria!")
        @FutureOrPresent(message = "La data di prenotazione deve essere nel futuro o nel presente!")
        LocalDate dataPrenotazione,

        @NotNull(message = "La data di fine prenotazione è obbligatoria!")
        @Future(message = "La data di fine prenotazione deve essere nel futuro!")
        LocalDate dataFinePrenotazione,

        @NotNull(message = "L'importo è obbligatorio")
        double prezzoPrenotazione

) {
}
