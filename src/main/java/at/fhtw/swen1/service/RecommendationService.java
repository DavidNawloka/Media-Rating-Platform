package at.fhtw.swen1.service;

import at.fhtw.swen1.model.Media;
import at.fhtw.swen1.model.User;
import at.fhtw.swen1.repository.MediaGenreRepository;
import at.fhtw.swen1.repository.RatingRepository;
import at.fhtw.swen1.repository.RecommendationRepository;
import at.fhtw.swen1.repository.UserRepository;

import java.util.ArrayList;

public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final MediaGenreRepository mediaGenreRepository;
    private final RatingRepository ratingRepository;

    public RecommendationService(RecommendationRepository recommendationRepository, UserRepository userRepository, MediaGenreRepository mediaGenreRepository, RatingRepository ratingRepository){

        this.recommendationRepository = recommendationRepository;
        this.userRepository = userRepository;
        this.mediaGenreRepository = mediaGenreRepository;
        this.ratingRepository = ratingRepository;
    }

    public ArrayList<Media> getRecommendations(int userId, boolean isGenreRecommendation){
        ArrayList<Integer> ratedMediaIds = recommendationRepository.getRatedMediaIds(userId);

        ArrayList<Media> mediaList;
        if(isGenreRecommendation){
            mediaList = getGenreRecommendations(userId, ratedMediaIds);
        }
        else{
            mediaList = getContentRecommendations(userId, ratedMediaIds);
        }

        for(Media media: mediaList){
            int[] genreIds = mediaGenreRepository.findGenreIdsByMediaId(media.getId());
            media.setGenreIds(genreIds);
            media.setAverageScore(ratingRepository.getAverageRating(media.getId()));
        }

        return mediaList;
    }

    private ArrayList<Media> getGenreRecommendations(int userId, ArrayList<Integer> excludeIds){
        User user = userRepository.findById(userId);
        if(user == null || user.getFavoriteGenreId() == null){
            return new ArrayList<>();
        }

        return recommendationRepository.getMediaByGenreExcluding(user.getFavoriteGenreId(), excludeIds);
    }

    private ArrayList<Media> getContentRecommendations(int userId, ArrayList<Integer> excludeIds){
        ArrayList<Integer> genreIds =recommendationRepository.getHighlyRatedGenreIds(userId);

        if(genreIds.isEmpty()){
            return new ArrayList<>();
        }

        return recommendationRepository.getMediaByGenresExcluding(genreIds,excludeIds);
    }

}
