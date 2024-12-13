package riccardomamoli.cib_db.payloads;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record NewUtenteDTO(

        @NotEmpty(message = "Il nome è obbligatorio!")
        @Size(min = 2, max = 40, message = "Il nome deve essere compreso tra 2 e 40 caratteri!")
        String nomeUtente,

        @NotEmpty(message = "Il cognome è obbligatorio!")
        @Size(min = 2, max = 40, message = "Il cognome deve essere compreso tra 2 e 40 caratteri!")
        String cognomeUtente,

        @NotEmpty(message = "Lo username è obbligatorio!")
        @Size(min = 2, max = 40, message = "Lo username deve essere compreso tra 2 e 40 caratteri!")
        String usernameUtente,

        @NotNull(message = "La data di nascita è obbligatoria!")
        LocalDate dataDiNascita,

        @NotEmpty(message = "L'email è obbligatoria!")
        @Email(message = "L'email inserita non è un'email valida!")
        String emailUtente,

        @NotEmpty(message = "La password è obbligatoria!")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "La password non segue i criteri comuni")
        String password,

        @NotEmpty(message = "L'indirizzo è obbligatorio!")
        @Size(min = 5, max = 100, message = "L'indirizzo deve essere compreso tra 5 e 100 caratteri!")
        String indirizzo,

        @NotEmpty(message = "La città è obbligatoria!")
        @Size(min = 2, max = 50, message = "La città deve essere compresa tra 2 e 50 caratteri!")
        String citta,

        @NotEmpty(message = "Lo stato è obbligatorio!")
        @Size(min = 2, max = 50, message = "Lo stato deve essere compreso tra 2 e 50 caratteri!")
        String stato,

        @NotEmpty(message = "Il codice postale è obbligatorio!")
        @Pattern(regexp = "^\\d{5}$", message = "Il codice postale deve essere composto da 5 cifre!")
        String codicePostale

) {
}
