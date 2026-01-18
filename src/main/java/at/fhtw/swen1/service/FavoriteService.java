package at.fhtw.swen1.service;

import at.fhtw.swen1.exception.AlreadyExistsException;
import at.fhtw.swen1.exception.NotExistsException;
import at.fhtw.swen1.repository.FavoriteRepository;
import at.fhtw.swen1.repository.MediaRepository;
import at.fhtw.swen1.repository.UnitOfWork;

public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MediaRepository mediaRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, MediaRepository mediaRepository) {
        this.favoriteRepository = favoriteRepository;
        this.mediaRepository = mediaRepository;
    }

    public void saveFavorite(int userId, int mediaId) throws AlreadyExistsException, NotExistsException {
        if(favoriteRepository.exists(userId, mediaId)){
            throw new AlreadyExistsException("Media already favorited");
        }

        if(mediaRepository.findById(mediaId) == null){
            throw new NotExistsException("Media does not exist");
        }

        try(UnitOfWork uow = new UnitOfWork()){
            favoriteRepository.save(userId, mediaId,uow);
            uow.commitTransaction();
        }

    }

    public void deleteFavorite(int userId, int mediaId) throws NotExistsException {
        if(!favoriteRepository.exists(userId, mediaId)){
            throw new NotExistsException("Media not favorited yet");
        }

        try(UnitOfWork uow = new UnitOfWork()){
            favoriteRepository.delete(userId, mediaId,uow);
            uow.commitTransaction();
        }

    }
}
