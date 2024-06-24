package airAstana.flightStatus.service.impl;

import airAstana.flightStatus.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Implementation of JwtService for JWT token generation, validation, and extraction.
 */
@Service
public class JwtServiceImpl implements JwtService {

    private final Key TOKEN_KEY = getSigningKey();

    /**
     * Generates a JWT token based on user details.
     *
     * @param userDetails User details to generate the token for
     * @return JWT token as a String
     */
    @Override
    @Operation(summary = "Generate JWT Token")
    public String generateToken(@Parameter(description = "User details for generating the token") UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // token valid for 24 hours
                .signWith(TOKEN_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token JWT token from which to extract the username
     * @return Username extracted from the token
     */
    @Override
    @Operation(summary = "Extract Username from JWT Token")
    public String extractUserName(@Parameter(description = "JWT token from which to extract the username") String token) {
        return extractClaims(token, Claims::getSubject);
    }

    /**
     * Extracts claims from a JWT token using a specified claims resolver function.
     *
     * @param token          JWT token from which to extract claims
     * @param claimsResolver Function to resolve specific claims from extracted JWT claims
     * @param <T>            Type of the claim to be extracted
     * @return Resolved claim value
     */
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token JWT token from which to extract claims
     * @return All claims extracted from the JWT token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(TOKEN_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the expiration date from a JWT token.
     *
     * @param token JWT token from which to retrieve the expiration date
     * @return Expiration date of the JWT token
     */
    private Date getExpirationDateFromToken(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    /**
     * Checks if a JWT token is expired.
     *
     * @param token JWT token to check for expiration
     * @return True if the token is expired, otherwise false
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Validates a JWT token against user details.
     *
     * @param token       JWT token to validate
     * @param userDetails User details against which to validate the token
     * @return True if the token is valid for the user, otherwise false
     */
    @Override
    @Operation(summary = "Validate JWT Token")
    public Boolean validateToken(@Parameter(description = "JWT token to validate") String token,
                                 @Parameter(description = "User details against which to validate the token") UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Retrieves a new signing key for JWT token generation.
     *
     * @return New signing key for JWT token generation
     */
    private Key getSigningKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }
}
