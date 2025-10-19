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

    public Session register(String username, String email, String password) throws UserAlreadyExistsException, ValidationException {
        if(!AuthValidationService.isValidUsername(username) || !AuthValidationService.isValidPassword(password) || !AuthValidationService.isValidEmail(email)){
            throw new ValidationException("Username and password are required");
        }
        if(!AuthValidationService.isSecurePassword(password)){
            throw new ValidationException("Password must be at least 6 characters");
        }

        if(userRepository.findByUsername(username) != null){
            throw new UserAlreadyExistsException("Username '" + username +"' already exists");
        }
        if(userRepository.findByEmail(email) != null){
            throw new UserAlreadyExistsException("Email '" + email +"' already exists");
        }

        String hashedPassword = HashUtil.hashString(password);

        User user = new User(username, email, hashedPassword);
        user = userRepository.save(user);
        return createNewSession(user.getId());
    }

    public Session login(String usernameOrEmail, String password) throws ValidationException, CredentialsException {
        if(!AuthValidationService.isValidUsernameOrEmail(usernameOrEmail) || !AuthValidationService.isValidPassword(password)){
            throw new ValidationException("Username and password are required");
        }

        User user = userRepository.findByUsername(usernameOrEmail);

        if(user == null){
            user = userRepository.findByEmail(usernameOrEmail);
            if(user == null){
                throw new CredentialsException("Invalid credentials");
            }
        }

        if(!HashUtil.isEqualStringHash(password, user.getHashedPassword())){
            throw new CredentialsException("Invalid credentials");
        }

        return createNewSession(user.getId());
    }

    public int getLoggedInUser(String token){
        Session session = sessionRepository.getSession(token);
        if(session == null ){
            return -1;
        }
        if(session.getExpiresAt().before(new Timestamp(System.currentTimeMillis()))){
            sessionRepository.delete(token);
            return -1;
        }

        return session.getUserId();
    }

    private Session createNewSession(int userId) {
        String token = TokenUtil.generateToken();
        Timestamp expirationDate = TokenUtil.getExpirationDate();

        Session newSession = new Session(token, userId, expirationDate);

        sessionRepository.save(newSession);
        return newSession;
    }


}
