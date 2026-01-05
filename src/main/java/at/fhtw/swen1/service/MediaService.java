package at.fhtw.swen1.service;

import at.fhtw.swen1.enums.MediaType;
import at.fhtw.swen1.exception.NotExistsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Media;
import at.fhtw.swen1.repository.FavoriteRepository;
import at.fhtw.swen1.repository.GenreRepository;
import at.fhtw.swen1.repository.MediaGenreRepository;
import at.fhtw.swen1.repository.MediaRepository;
import at.fhtw.swen1.service.validation.ValidationService;

import java.util.ArrayList;

public class MediaService {
    private final MediaRepository mediaRepository;
    private final GenreRepository genreRepository;
    private final MediaGenreRepository mediaGenreRepository;
    private final FavoriteRepository favoriteRepository;

    public MediaService(MediaRepository mediaRepository, GenreRepository genreRepository, MediaGenreRepository mediaGenreRepository, FavoriteRepository favoriteRepository) {
        this.mediaRepository = mediaRepository;
        this.genreRepository = genreRepository;
        this.mediaGenreRepository = mediaGenreRepository;
        this.favoriteRepository = favoriteRepository;
    }

    public Media createMedia(String title, String description, MediaType mediaType, int releaseYear, int ageRestriction, int[] genreIds, int creatorId) throws NotExistsException, ValidationException {
        if(ValidationService.isNullOrEmpty(title) || ValidationService.isNullOrEmpty(description) || mediaType == null || releaseYear < 0 || ageRestriction < 0 || genreIds == null || creatorId < 0){
            throw new ValidationException("Invalid media data");
        }

        for(int genreId : genreIds){
            if(genreRepository.getGenre(genreId) == null){
                throw new NotExistsException("Genre ID: " + genreId + " does not exist.");
            }
        }

        Media media = new Media(title, description, mediaType, releaseYear, ageRestriction, genreIds, creatorId);
        Media createdMedia = mediaRepository.save(media);

        for(int genreId : genreIds){
            mediaGenreRepository.save(createdMedia.getId(), genreId);
        }

        return createdMedia;
    }

    public ArrayList<Media> getFavoriteMedias(int userId){
        return favoriteRepository.findByUserId(userId);
    }

    public Media getMedia(int mediaId, int loggedInUserId, boolean onlyOwner) throws NotExistsException{
        Media media = mediaRepository.findById(mediaId);
        if(media == null || (onlyOwner && media.getCreatorId() != loggedInUserId) ){
            throw new NotExistsException("Media with ID: " + mediaId + " does not exist.");
        }

        int[] genreIds = mediaGenreRepository.findGenreIdsByMediaId(mediaId);
        media.setGenreIds(genreIds);
        return media;
    }

    public Media updateMedia(int loggedInUserId, int mediaId,String title, String description, MediaType mediaType, int releaseYear, int ageRestriction, int[] genreIds, int creatorId) throws ValidationException, NotExistsException {
        if(ValidationService.isNullOrEmpty(title) || ValidationService.isNullOrEmpty(description) || mediaType == null || releaseYear < 0 || ageRestriction < 0 || genreIds == null || creatorId < 0){
            throw new ValidationException("Invalid media data");
        }

        for(int genreId : genreIds){
            if(genreRepository.getGenre(genreId) == null){
                throw new NotExistsException("Genre ID: " + genreId + " does not exist.");
            }
        }

        Media existingMedia = getMedia(mediaId,  loggedInUserId, true);



        int[] existingGenreIds = existingMedia.getGenreIds();

        // Remove genres that are no longer in the new list
        for(int existingId : existingGenreIds){
            boolean shouldRemove = java.util.Arrays.stream(genreIds)
                    .noneMatch(id -> id == existingId);
            if(shouldRemove){
                mediaGenreRepository.delete(existingMedia.getId(), existingId);
            }
        }

        // Add genres that do not exist in the list yet
        for(int newId : genreIds){
            boolean shouldAdd = java.util.Arrays.stream(existingGenreIds)
                    .noneMatch(id -> id == newId);
            if(shouldAdd){
                mediaGenreRepository.save(existingMedia.getId(), newId);
            }
        }

        Media media = new Media(mediaId, title, description, mediaType, releaseYear, ageRestriction, genreIds, creatorId);

        return mediaRepository.update(media);
    }

    public void deleteMedia(int mediaId, int loggedInUserId) throws NotExistsException{
        Media media = mediaRepository.findById(mediaId);
        if(media == null || media.getCreatorId() != loggedInUserId ){
            throw new NotExistsException("Media with ID: " + mediaId + " does not exist.");
        }

        mediaRepository.delete(mediaId);
    }
}
