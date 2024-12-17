package riccardomamoli.cib_db.payloads;

import jakarta.validation.constraints.*;

public record NewRecensioneDTO(

        @NotNull(message = "L'ID della prenotazione è obbligatorio!")
        Long prenotazioneId,

        @NotNull(message = "L'ID dell'utente è obbligatorio!")
        Long utenteId,

        @NotEmpty(message = "Il testo della recensione è obbligatorio!")
        @Size(min = 5, max = 500, message = "Il testo della recensione deve essere compreso tra 5 e 500 caratteri!")
        String testoRecensione,

        @Min(value = 1, message = "La valutazione deve essere compresa tra 1 e 5!")
        @Max(value = 5, message = "La valutazione deve essere compresa tra 1 e 5!")
        Integer valutazioneRecensione
) {
}

