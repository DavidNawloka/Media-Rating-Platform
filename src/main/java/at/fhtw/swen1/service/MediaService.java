package at.fhtw.swen1.service;

import at.fhtw.swen1.enums.MediaType;
import at.fhtw.swen1.exception.GenreNotExistsException;
import at.fhtw.swen1.model.Media;
import at.fhtw.swen1.repository.GenreRepository;
import at.fhtw.swen1.repository.MediaGenreRepository;
import at.fhtw.swen1.repository.MediaRepository;
import at.fhtw.swen1.service.validation.ValidationService;

public class MediaService {
    private MediaRepository mediaRepository;
    private GenreRepository genreRepository;
    private MediaGenreRepository mediaGenreRepository;

    public MediaService(MediaRepository mediaRepository, GenreRepository genreRepository, MediaGenreRepository mediaGenreRepository) {
        this.mediaRepository = mediaRepository;
        this.genreRepository = genreRepository;
        this.mediaGenreRepository = mediaGenreRepository;
    }

    public Media createMedia(String title, String description, MediaType mediaType, int releaseYear, int ageRestriction, int[] genreIds, int creatorId) throws IllegalArgumentException, GenreNotExistsException {
        if(ValidationService.isNullOrEmpty(title) || ValidationService.isNullOrEmpty(description) || mediaType == null || releaseYear < 0 || ageRestriction < 0 || genreIds == null || creatorId < 0){
            throw new IllegalArgumentException("Invalid media data");
        }

        for(int genreId : genreIds){
            if(genreRepository.getGenre(genreId) == null){
                throw new GenreNotExistsException("Genre ID: " + genreId + " does not exist.");
            }
        }

        Media media = new Media(title, description, mediaType, releaseYear, ageRestriction, genreIds, creatorId);
        Media createdMedia = mediaRepository.createMedia(media);

        for(int genreId : genreIds){
            mediaGenreRepository.addMediaGenre(createdMedia.getId(), genreId);
        }

        return createdMedia;
    }
}
