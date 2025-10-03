package com.alfredamos.springmanagementofemployees.services;

import com.alfredamos.springmanagementofemployees.configs.JwtConfig;
import com.alfredamos.springmanagementofemployees.dto.*;
import com.alfredamos.springmanagementofemployees.entities.Employee;
import com.alfredamos.springmanagementofemployees.entities.Token;
import com.alfredamos.springmanagementofemployees.entities.TokenType;
import com.alfredamos.springmanagementofemployees.entities.User;
import com.alfredamos.springmanagementofemployees.exceptions.BadRequestException;
import com.alfredamos.springmanagementofemployees.exceptions.UnAuthorizedException;
import com.alfredamos.springmanagementofemployees.mapper.AuthMapper;
import com.alfredamos.springmanagementofemployees.repositories.AuthRepository;
import com.alfredamos.springmanagementofemployees.repositories.EmployeeRepository;
import com.alfredamos.springmanagementofemployees.repositories.UserRepository;
import com.alfredamos.springmanagementofemployees.utils.AuthParams;
import com.alfredamos.springmanagementofemployees.utils.CookieParameter;
import com.alfredamos.springmanagementofemployees.utils.ResponseMessage;
import com.alfredamos.springmanagementofemployees.utils.SetCookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;
    private final AuthRepository authRepository;
    private final TokenService tokenService;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    ////----> Change user password method.
    public ResponseMessage changePassword(ChangePassword changePassword) {
        //----> Check for passwords match.
        if (isNotMatchPassword(changePassword.getConfirmPassword(), changePassword.getNewPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        //----> Check for existence of user.
        var user = fetchUserByEmail(changePassword.getEmail());
        if (user == null) {
            throw new UnAuthorizedException("Invalid credentials");
        }

        //----> Check for valid password.
        if (isNotValidPassword(changePassword.getOldPassword(), user.getPassword())) {
            throw new UnAuthorizedException("Invalid credentials");
        }

        //----> Hash the new password.
        var hashedPassword = passwordEncoder.encode(changePassword.getNewPassword());
        user.setPassword(hashedPassword);

        //----> Save the changes in the database.
        userRepository.save(user);

        //----> Send back the response.
        return new ResponseMessage("Password has ben changed successfully!", "success", HttpStatus.OK);

    }

    ////----> Change name of user method.
    public ResponseMessage changeName(ChangeName changeName) {
        //----> Check for existence of user.
        var user = fetchUserByEmail(changeName.getEmail());
        if (user == null) {
            throw new UnAuthorizedException("Invalid credentials");
        }

        //----> Check for valid password.
        if (isNotValidPassword(changeName.getPassword(), user.getPassword())) {
            throw new UnAuthorizedException("Invalid credentials");
        }

        //----> Set the new name on user.
        user.setName(changeName.getName());

        //----> Get the employee associated with this user.
        var employee = fetchEmployeeByEmail(user.getEmail());
        employee.setName(changeName.getName());

        //----> Save the change in name to both user and employee database.
        employeeRepository.save(employee);
        userRepository.save(user);

        //----> Send back the response.
        return new ResponseMessage("Name has been changed successfully!", "success", HttpStatus.OK);
    }

    ////----> Change user image method.
    public ResponseMessage changeImage(ChangeImage changeImage) {
        //----> Check for existence of user.
        var user = fetchUserByEmail(changeImage.getEmail());
        if (user == null) {
            throw new UnAuthorizedException("Invalid credentials");
        }

        //----> Check for valid password.
        if (isNotValidPassword(changeImage.getPassword(), user.getPassword())) {
            throw new UnAuthorizedException("Invalid credentials");
        }

        //----> Set the new image on user.
        user.setImage(changeImage.getImage());

        //----> Get the employee associated with this user.
        var employee = fetchEmployeeByEmail(user.getEmail());
        employee.setImage(changeImage.getImage());

        //----> Save the change in image to both user and employee database.
        employeeRepository.save(employee);
        userRepository.save(user);

        //----> Send back the response.
        return new ResponseMessage("Image has been changed successfully!", "success", HttpStatus.OK);
    }

    ////----> Get current logged-in user function.
    public User getCurrentLoggedInUser(){
        //----> Get authentication object from security context holder.
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        //----> Get email from the principal on authentication
        //----> and fetch the current logged-in user from database.
        var email = (String) authentication.getPrincipal();
        var user = authRepository.findUserByEmail(email);

        //----> Unauthenticated user.
        if (user == null){
            throw  new UnAuthorizedException("Invalid credential!");
        }

        //----> Send back the current logged-in user.
        return user;
    }

    ////----> Login user function.
    public String loginUser(Login login, HttpServletResponse response) {
        //----> Authenticate user.
        var loginAction = new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword());

        //----> Authenticate user.
        authenticationManager.authenticate(loginAction);

        //----> Get the authenticated user.
        var user = authRepository.findUserByEmail(login.getEmail());

        //----> Generate access and refresh tokens and store them in cookies.
        var accessToken = generateAccessAndRefreshTokensAndCookies(response, user);

        //----> Send back the response.
        return accessToken.toString();
    }

    ////----> Refresh user token method.
    public String refreshUserToken(String refreshToken, HttpServletResponse response) {
        //----> Check token validity.
        var jwt = jwtService.parseToken(refreshToken);

        //----> Check the validity of jwt.
        if (jwt == null) {
            throw new UnAuthorizedException("Invalid refresh token!");
        }

        //----> Get the authenticated user and revoke all valid tokens.
        var email = jwt.getUserEmail();
        var user = authRepository.findUserByEmail(email);
        tokenService.revokedAllTokensByUserId(user.getId());

        //----> Generate access and refresh tokens and store them in cookies.
        var accessToken = generateAccessAndRefreshTokensAndCookies(response, user);

        //----> Send back the response.
        return accessToken.toString();
    }

    ////----> signup user method.
    @Transactional
    public ResponseMessage signupUser(Signup signup){
        //----> Check for password match.
        if (isNotMatchPassword(signup.getPassword(), signup.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        //----> Check for existence of user.
        var user = fetchUserByEmail(signup.getEmail());
        System.out.println("In auth-service-signup, user: " + user);
        if (user != null) {
            throw new UnAuthorizedException("Invalid credentials");
        }

        //----> Hash the password.
        var hashedPassword = passwordEncoder.encode(signup.getPassword());
        signup.setPassword(hashedPassword);

        //----> save both the employee and user in their databases.
        var newUser = userRepository.save(authMapper.toUserCreateEntity(signup));
        employeeRepository.save(authMapper.toEmployeeCreateEntity(signup, newUser));

        //----> send back the response.
        return new ResponseMessage("User and employee have been created successfully!", "success", HttpStatus.OK);

    }

    ////----> Check for passwords match method.
    private boolean isNotMatchPassword(String passwordOne, String passwordTwo) {
        return !passwordOne.equals(passwordTwo);
    }

    ////----> Check for valid password method.
    private boolean isNotValidPassword(CharSequence rawPassword, String encodedPassword) {
        return !passwordEncoder.matches(rawPassword, encodedPassword);
    }

    ////----> Fetch user by email method.
    private User fetchUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    ////----. Fetch employee by email method.
    private Employee fetchEmployeeByEmail(String email) {
        return employeeRepository.findEmployeeByEmail(email);
    }

    ////----> Generate access and refresh tokens, create new token object and store the access and refresh tokens in cookies method.
    private Jwt generateAccessAndRefreshTokensAndCookies(HttpServletResponse response, User user) {
        //----> Revoked all valid tokens
        tokenService.revokedAllTokensByUserId(user.getId());

        //----> Generate access token and store it on cookie on response object.
        var accessToken = jwtService.generateAccessToken(user);
        var accessTokenCookie = SetCookie.makeCookie(new CookieParameter(AuthParams.accessToken, accessToken, (int)jwtConfig.getAccessTokenExpiration(), AuthParams.accessTokenPath));
        response.addCookie(accessTokenCookie);

        //----> Generate refresh token and store it on cookie on response object.
        var refreshToken = jwtService.generateRefreshToken(user);
        var refreshTokenCookie = SetCookie.makeCookie(new CookieParameter(AuthParams.refreshToken, refreshToken, (int)this.jwtConfig.getRefreshTokenExpiration(), AuthParams.refreshTokenPath));
        response.addCookie(refreshTokenCookie);

        //----> Set new token object.
        var token = makeNewToken(user, accessToken.toString(), refreshToken.toString());

        //----> Store the newly generated token object in the database
        tokenService.createToken(token);
        return accessToken;
    }

    ////----> Set new token method.
    private Token makeNewToken(User user, String accessToken, String refreshToken) {
        //----> Initialize token
        var token = new Token();
        //----> Set all the values into token object
        token.setExpired(false);
        token.setRevoked(false);
        token.setUser(user);
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setTokenType(TokenType.Bearer);

        //----> Send back the results.
        return token;
    }
}
