package at.fhtw.swen1.service;

import at.fhtw.swen1.enums.MediaType;
import at.fhtw.swen1.exception.NotExistsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Media;
import at.fhtw.swen1.repository.*;
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
        if(ValidationService.isNullOrEmpty(title) || ValidationService.isNullOrEmpty(description) || mediaType == null || releaseYear < 0 || ageRestriction < 0 || genreIds == null || creatorId < 0 || genreIds.length == 0){
            throw new ValidationException("Invalid media data");
        }

        for(int genreId : genreIds){
            if(genreRepository.getGenre(genreId) == null){
                throw new NotExistsException("Genre ID: " + genreId + " does not exist.");
            }
        }

        Media media = new Media(title, description, mediaType, releaseYear, ageRestriction, genreIds, creatorId);
        try(UnitOfWork uow = new UnitOfWork()){
            Media createdMedia = mediaRepository.save(media,uow);

            for(int genreId : genreIds){
                mediaGenreRepository.save(createdMedia.getId(), genreId,uow);
            }

            uow.commitTransaction();
            return createdMedia;
        }

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

    public Media updateMedia(int loggedInUserId, int mediaId,String title, String description, MediaType mediaType, int releaseYear, int ageRestriction, int[] genreIds) throws ValidationException, NotExistsException {
        if(ValidationService.isNullOrEmpty(title) || ValidationService.isNullOrEmpty(description) || mediaType == null || releaseYear < 0 || ageRestriction < 0 || genreIds == null || genreIds.length == 0){
            throw new ValidationException("Invalid media data");
        }

        for(int genreId : genreIds){
            if(genreRepository.getGenre(genreId) == null){
                throw new NotExistsException("Genre ID: " + genreId + " does not exist.");
            }
        }

        Media existingMedia = getMedia(mediaId,  loggedInUserId, true);



        int[] existingGenreIds = existingMedia.getGenreIds();

        try(UnitOfWork uow = new UnitOfWork()){
            // Remove genres that are no longer in the new list
            for(int existingId : existingGenreIds){
                boolean shouldRemove = java.util.Arrays.stream(genreIds)
                        .noneMatch(id -> id == existingId);
                if(shouldRemove){
                    mediaGenreRepository.delete(existingMedia.getId(), existingId, uow);
                }
            }

            // Add genres that do not exist in the list yet
            for(int newId : genreIds){
                boolean shouldAdd = java.util.Arrays.stream(existingGenreIds)
                        .noneMatch(id -> id == newId);
                if(shouldAdd){
                    mediaGenreRepository.save(existingMedia.getId(), newId, uow);
                }
            }

            Media media = new Media(mediaId, title, description, mediaType, releaseYear, ageRestriction, genreIds, existingMedia.getCreatorId(),existingMedia.getAverageScore());

            Media newMedia = mediaRepository.update(media, uow);
            uow.commitTransaction();
            return newMedia;
        }

    }

    public void deleteMedia(int mediaId, int loggedInUserId) throws NotExistsException{
        Media media = mediaRepository.findById(mediaId);
        if(media == null || media.getCreatorId() != loggedInUserId ){
            throw new NotExistsException("Media with ID: " + mediaId + " does not exist.");
        }
        try(UnitOfWork uow = new UnitOfWork()){
            mediaRepository.delete(mediaId,uow);
            uow.commitTransaction();
        }

    }

    public ArrayList<Media> getMediaList(String title, String genreId, String mediaType, String releaseYear, String ageRestriction, String rating, String sortBy) {
        ArrayList<Media> mediaList = mediaRepository.findAllWithFilters(title,genreId, mediaType, releaseYear, ageRestriction, rating, sortBy);

        for(Media media: mediaList){
            int[] genreIds = mediaGenreRepository.findGenreIdsByMediaId(media.getId());
            media.setGenreIds(genreIds);
        }
        return mediaList;
    }
}
