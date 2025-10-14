package at.fhtw.swen1.service;

import at.fhtw.swen1.enums.MediaType;
import at.fhtw.swen1.exception.GenreNotExistsException;
import at.fhtw.swen1.exception.MediaNotExistsException;
import at.fhtw.swen1.exception.ValidationException;
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

    public Media createMedia(String title, String description, MediaType mediaType, int releaseYear, int ageRestriction, int[] genreIds, int creatorId) throws GenreNotExistsException, ValidationException {
        if(ValidationService.isNullOrEmpty(title) || ValidationService.isNullOrEmpty(description) || mediaType == null || releaseYear < 0 || ageRestriction < 0 || genreIds == null || creatorId < 0){
            throw new ValidationException("Invalid media data");
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

    public Media getMedia(int mediaId, int loggedInUserId) throws MediaNotExistsException{
        Media media = mediaRepository.findyById(mediaId);
        if(media == null || media.getCreatorId() != loggedInUserId ){
            throw new MediaNotExistsException("Media with ID: " + mediaId + " does not exist.");
        }

        int[] genreIds = mediaGenreRepository.findGenreIdsByMediaId(mediaId);
        media.setGenreIds(genreIds);
        return media;
    }

    public Media updateMedia(int loggedInUserId, int mediaId,String title, String description, MediaType mediaType, int releaseYear, int ageRestriction, int[] genreIds, int creatorId) throws ValidationException, GenreNotExistsException,  MediaNotExistsException {
        if(ValidationService.isNullOrEmpty(title) || ValidationService.isNullOrEmpty(description) || mediaType == null || releaseYear < 0 || ageRestriction < 0 || genreIds == null || creatorId < 0){
            throw new ValidationException("Invalid media data");
        }

        for(int genreId : genreIds){
            if(genreRepository.getGenre(genreId) == null){
                throw new GenreNotExistsException("Genre ID: " + genreId + " does not exist.");
            }
        }

        Media existingMedia = getMedia(mediaId,  loggedInUserId);



        int[] existingGenreIds = existingMedia.getGenreIds();

        // Remove genres that are no longer in the new list
        for(int existingId : existingGenreIds){
            boolean shouldRemove = java.util.Arrays.stream(genreIds)
                    .noneMatch(id -> id == existingId);
            if(shouldRemove){
                mediaGenreRepository.removeMediaGenre(existingMedia.getId(), existingId);
            }
        }

        // Add genres that do not exist in the list yet
        for(int newId : genreIds){
            boolean shouldAdd = java.util.Arrays.stream(existingGenreIds)
                    .noneMatch(id -> id == newId);
            if(shouldAdd){
                mediaGenreRepository.addMediaGenre(existingMedia.getId(), newId);
            }
        }

        Media media = new Media(mediaId, title, description, mediaType, releaseYear, ageRestriction, genreIds, creatorId);

        return mediaRepository.updateMedia(media);
    }

    public void deleteMedia(int mediaId, int loggedInUserId) throws MediaNotExistsException{
        Media media = mediaRepository.findyById(mediaId);
        if(media == null || media.getCreatorId() != loggedInUserId ){
            throw new MediaNotExistsException("Media with ID: " + mediaId + " does not exist.");
        }

        mediaRepository.deleteMedia(mediaId);
    }
}
