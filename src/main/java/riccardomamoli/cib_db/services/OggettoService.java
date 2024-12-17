package riccardomamoli.cib_db.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import riccardomamoli.cib_db.entities.*;
import riccardomamoli.cib_db.exceptions.BadRequestException;
import riccardomamoli.cib_db.exceptions.NotFoundException;
import riccardomamoli.cib_db.payloads.NewOggettoDTO;
import riccardomamoli.cib_db.repositories.CategoriaRepository;
import riccardomamoli.cib_db.repositories.OggettoRepository;
import riccardomamoli.cib_db.repositories.UtenteRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OggettoService {

    @Autowired
    private OggettoRepository oggettoRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private Cloudinary cloudinaryUploader;


    public Page<Oggetto> findWithFilters(Long id, String nomeOggetto, Boolean disponibilita, Double prezzoOggetto, Pageable pageable) {
        Specification<Oggetto> specs = Specification.where(null);

        if (id != null) {
            specs = specs.and(OggettoSpecifications.hasUtenteId(id));
        }
        if (nomeOggetto != null) {
            specs = specs.and(OggettoSpecifications.hasNomeOggetto(nomeOggetto));
        }

        if (disponibilita != null) {
            specs = specs.and(OggettoSpecifications.hasDisponibilita(disponibilita));
        }
        if (prezzoOggetto != null) {
            specs = specs.and(OggettoSpecifications.hasPrezzoOggetto(prezzoOggetto));
        }
        return oggettoRepository.findAll(specs, pageable);
    }

    public String uploadFotoOggetto(MultipartFile file, Long idOggetto) {
        try {

            String url = (String) cloudinaryUploader.uploader()
                    .upload(file.getBytes(), ObjectUtils.emptyMap())
                    .get("url");

            Oggetto found = oggettoRepository.findById(idOggetto)
                    .orElseThrow(() -> new EntityNotFoundException("Oggetto con ID " + idOggetto + " non trovato!"));

            if (found.getFotoUrls().size() >= 3) {
                throw new IllegalStateException("Puoi aggiungere massimo 3 foto per oggetto!");
            }

            found.addFotoUrl(url);
            oggettoRepository.save(found);

            return url;
        } catch (IOException err) {
            throw new BadRequestException("Ci sono stati problemi con l'upload del file!");
        }
    }


    public Oggetto createOggetto(NewOggettoDTO body) {
        if (oggettoRepository.existsByNomeOggetto(body.nomeOggetto())) {
            throw new BadRequestException("Nome già utilizzato!");
        }

        Optional<Utente> utenteFound = utenteRepository.findById(body.utenteId());

        if (utenteFound.isEmpty()) {
            throw new NotFoundException("Utente non trovato!");
        }

        List<Categoria> categorie = new ArrayList<>();
        for (String categoriaNome : body.categorie()) {
            Categoria categoria = categoriaRepository.findByCategoria(categoriaNome)
                    .orElseThrow(() -> new NotFoundException("Categoria non trovata: " + categoriaNome));
            categorie.add(categoria);
        }


        List<String> fotoUrls = body.fotoUrls();
        if (fotoUrls == null || fotoUrls.isEmpty()) {

            fotoUrls = new ArrayList<>();
            fotoUrls.add("https://res.cloudinary.com/dcrdtbcyz/image/upload/v1734004021/placeholder-image_hxxqav.jpg");
            fotoUrls.add("https://res.cloudinary.com/dcrdtbcyz/image/upload/v1734004021/placeholder-image_hxxqav.jpg");
            fotoUrls.add("https://res.cloudinary.com/dcrdtbcyz/image/upload/v1734004021/placeholder-image_hxxqav.jpg");
        }

        Oggetto oggetto = new Oggetto(
                utenteFound.get(),
                body.nomeOggetto(),
                body.descrizioneOggetto(),
                body.disponibilita(),
                body.prezzoGiornaliero(),
                categorie
        );

        oggetto.setFotoUrls(fotoUrls);

        return oggettoRepository.save(oggetto);
    }


    public Oggetto findById(Long id) {
        return oggettoRepository.findById(id).orElseThrow(() -> new NotFoundException("Nessun oggetto trovato con questo ID: " + id));
    }

    public void findByIdAndDelete(Long id) {
        Oggetto found = this.findById(id);
        oggettoRepository.delete(found);
    }

    public Oggetto findByIdAndUpdate(Long id, NewOggettoDTO body) {

        Oggetto oggetto = this.findById(id);

        if (!body.utenteId().equals(oggetto.getUtente().getId())) {
            Optional<Utente> utenteFound = utenteRepository.findById(body.utenteId());
            if (utenteFound.isEmpty()) {
                throw new NotFoundException("Utente non trovato!");
            }
            oggetto.setUtente(utenteFound.get());
        }


        if (!body.nomeOggetto().equals(oggetto.getNomeOggetto())) {
            oggetto.setNomeOggetto(body.nomeOggetto());
        }


        if (!body.descrizioneOggetto().equals(oggetto.getDescrizioneOggetto())) {
            oggetto.setDescrizioneOggetto(body.descrizioneOggetto());
        }

        if (!body.disponibilita().equals(oggetto.getDisponibilita())) {
            oggetto.setDisponibilita(body.disponibilita());
        }


        if (body.prezzoGiornaliero() != oggetto.getPrezzoGiornaliero()) {
            oggetto.setPrezzoGiornaliero(body.prezzoGiornaliero());
        }

        if (body.categorie() != null && !body.categorie().isEmpty()) {
            List<Categoria> nuoveCategorie = new ArrayList<>();
            for (String categoriaNome : body.categorie()) {
                Categoria categoria = categoriaRepository.findByCategoria(categoriaNome)
                        .orElseThrow(() -> new NotFoundException("Categoria non trovata: " + categoriaNome));
                nuoveCategorie.add(categoria);
            }


            List<CategoriaOggetto> nuoveCategorieOggetto = nuoveCategorie.stream()
                    .map(categoria -> new CategoriaOggetto(categoria, oggetto))
                    .toList();


            if (!nuoveCategorieOggetto.equals(oggetto.getOggettoCategorie())) {
                oggetto.getOggettoCategorie().clear();
                oggetto.getOggettoCategorie().addAll(nuoveCategorieOggetto);
            }

            List<String> fotoUrls = body.fotoUrls();
            if (fotoUrls == null || fotoUrls.isEmpty()) {
                fotoUrls = new ArrayList<>();
                fotoUrls.add("https://res.cloudinary.com/dcrdtbcyz/image/upload/v1734004021/placeholder-image_hxxqav.jpg");
                fotoUrls.add("https://res.cloudinary.com/dcrdtbcyz/image/upload/v1734004021/placeholder-image_hxxqav.jpg");
                fotoUrls.add("https://res.cloudinary.com/dcrdtbcyz/image/upload/v1734004021/placeholder-image_hxxqav.jpg");
            }
            oggetto.setFotoUrls(fotoUrls);
        }


        return oggettoRepository.save(oggetto);
    }

}
