package at.fhtw.swen1.service;

import at.fhtw.swen1.exception.CredentialsException;
import at.fhtw.swen1.exception.UserAlreadyExistsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Session;
import at.fhtw.swen1.model.User;
import at.fhtw.swen1.repository.SessionRepository;
import at.fhtw.swen1.repository.UserRepository;
import at.fhtw.swen1.service.validation.AuthValidationService;
import at.fhtw.swen1.util.TokenUtil;
import at.fhtw.swen1.util.HashUtil;

import java.sql.Timestamp;


public class AuthService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public User register(String username, String password) throws UserAlreadyExistsException, ValidationException {
        if(!AuthValidationService.isValidUsername(username) || !AuthValidationService.isValidPassword(password) ){
            throw new ValidationException("Username and password are required");
        }
        if(!AuthValidationService.isSecurePassword(password)){
            throw new ValidationException("Password must be at least 6 characters");
        }

        if(userRepository.findByUsername(username) != null){
            throw new UserAlreadyExistsException("Username '" + username +"' already exists");
        }

        String hashedPassword = HashUtil.hashString(password);

        User user = new User(username, hashedPassword);
        return userRepository.save(user);
    }

    public Session login(String username, String password) throws ValidationException, CredentialsException {
        if(!AuthValidationService.isValidUsername(username) || !AuthValidationService.isValidPassword(password)){
            throw new ValidationException("Username and password are required");
        }

        User user = userRepository.findByUsername(username);

        if(user == null){
            throw new CredentialsException("Invalid credentials");
        }

        if(!HashUtil.isEqualStringHash(password, user.getHashedPassword())){
            throw new CredentialsException("Invalid credentials");
        }

        String token = TokenUtil.generateToken();
        Timestamp expirationDate = TokenUtil.getExpirationDate();

        Session newSession = new Session(token, user.getId(), expirationDate);

        sessionRepository.createSession(newSession);

        return newSession;
    }
}
