package riccardomamoli.cib_db.payloads;

import jakarta.validation.constraints.*;
import java.util.List;

public record NewOggettoDTO(

        @NotNull(message = "L'ID dell'utente è obbligatorio!")
        Long utenteId,

        @NotEmpty(message = "Il nome dell'oggetto è obbligatorio!")
        @Size(min = 2, max = 100, message = "Il nome dell'oggetto deve essere compreso tra 2 e 100 caratteri!")
        String nomeOggetto,

        @NotEmpty(message = "La descrizione dell'oggetto è obbligatoria!")
        @Size(min = 5, max = 500, message = "La descrizione deve essere compresa tra 5 e 500 caratteri!")
        String descrizioneOggetto,

        @NotNull(message = "La disponibilità è obbligatoria!")
        Boolean disponibilita,

        @DecimalMin(value = "0.0", inclusive = false, message = "Il prezzo orario deve essere maggiore di zero!")
        double prezzoGiornaliero,

        @NotEmpty(message = "Le categorie sono obbligatorie!")
        List<String> categorie,

        List<String> fotoUrls

) {
}
