package at.fhtw.swen1.service;

import at.fhtw.swen1.exception.AlreadyExistsException;
import at.fhtw.swen1.exception.NotExistsException;
import at.fhtw.swen1.repository.FavoriteRepository;
import at.fhtw.swen1.repository.MediaRepository;

public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MediaRepository mediaRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, MediaRepository mediaRepository) {
        this.favoriteRepository = favoriteRepository;
        this.mediaRepository = mediaRepository;
    }

    public void saveFavorite(int userId, int mediaId) throws AlreadyExistsException, NotExistsException {
        if(favoriteRepository.exists(userId, mediaId)){
            throw new AlreadyExistsException("Rating does already exists");
        }

        if(mediaRepository.findById(mediaId) == null){
            throw new NotExistsException("Media does not exist");
        }

        favoriteRepository.save(userId, mediaId);
    }

    public void deleteFavorite(int userId, int mediaId) throws NotExistsException {
        if(!favoriteRepository.exists(userId, mediaId)){
            throw new NotExistsException("Rating does not exist");
        }

        favoriteRepository.delete(userId, mediaId);
    }
}
