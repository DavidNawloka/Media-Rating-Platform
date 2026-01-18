package at.fhtw.swen1.service;

import at.fhtw.swen1.model.Media;
import at.fhtw.swen1.model.User;
import at.fhtw.swen1.repository.RecommendationRepository;
import at.fhtw.swen1.repository.UserRepository;

import java.util.ArrayList;

public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;

    public RecommendationService(RecommendationRepository recommendationRepository, UserRepository userRepository){

        this.recommendationRepository = recommendationRepository;
        this.userRepository = userRepository;
    }

    public ArrayList<Media> getRecommendations(int userId, boolean isGenreRecommendation){
        ArrayList<Integer> ratedMediaIds = recommendationRepository.getRatedMediaIds(userId);

        if(isGenreRecommendation){
            return getGenreRecommendations(userId, ratedMediaIds);
        }
        else{
            return getContentRecommendations(userId, ratedMediaIds);
        }
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
