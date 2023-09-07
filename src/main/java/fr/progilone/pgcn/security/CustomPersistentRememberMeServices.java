package fr.progilone.pgcn.security;

import fr.progilone.pgcn.domain.security.PersistentToken;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.repository.security.PersistentTokenRepository;
import fr.progilone.pgcn.repository.user.UserRepository;
import fr.progilone.pgcn.service.util.DateUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's RememberMeServices.
 * <p/>
 * Persistent tokens are used by Spring Security to automatically log in users.
 * <p/>
 * This is a specific implementation of Spring Security's remember-me authentication, but it is much more powerful than the standard implementations:
 * <ul>
 * <li>It allows a user to see the list of his currently opened sessions, and invalidate them</li>
 * <li>It stores more information, such as the IP address and the user agent, for audit purposes
 * <li>
 * <li>When a user logs out, only his current session is invalidated, and not all of his sessions</li>
 * </ul>
 * <p/>
 * This is inspired by:
 * <ul>
 * <li><a href="http://jaspan.com/improved_persistent_login_cookie_best_practice">Improved Persistent Login Cookie Best Practice</a></li>
 * <li><a href="https://github.com/blog/1661-modeling-your-app-s-user-session">Github's "Modeling your App's User Session"</a></li></li>
 * </ul>
 * <p/>
 * The main algorithm comes from Spring Security's PersistentTokenBasedRememberMeServices, but this class couldn't be cleanly extended.
 * <p/>
 */
@Service
public class CustomPersistentRememberMeServices extends AbstractRememberMeServices {

    private static final Logger LOG = LoggerFactory.getLogger(CustomPersistentRememberMeServices.class);

    // Token is valid for one month
    private static final int TOKEN_VALIDITY_DAYS = 31;

    private static final int DEFAULT_SERIES_LENGTH = 16;

    private static final int DEFAULT_TOKEN_LENGTH = 16;

    private static final long UPGRADED_TOKEN_VALIDITY_MILLIS = 5000l;

    private final PersistentTokenCache<UpgradedRememberMeToken> upgradedTokenCache;

    private final SecureRandom random;

    private final PersistentTokenRepository persistentTokenRepository;

    private final UserRepository userRepository;

    public CustomPersistentRememberMeServices(final Environment env,
                                              final org.springframework.security.core.userdetails.UserDetailsService userDetailsService,
                                              final UserRepository userRepository,
                                              final PersistentTokenRepository persistentTokenRepository) {

        super(env.getProperty("jhipster.security.rememberme.key"), userDetailsService);
        random = new SecureRandom();
        this.userRepository = userRepository;
        this.persistentTokenRepository = persistentTokenRepository;
        this.upgradedTokenCache = new PersistentTokenCache<>(UPGRADED_TOKEN_VALIDITY_MILLIS);
    }

    @Override
    protected UserDetails processAutoLoginCookie(final String[] cookieTokens, final HttpServletRequest request, final HttpServletResponse response) {

        synchronized (this) { // prevent 2 authentication requests from the same user in parallel
            String login = null;
            final UpgradedRememberMeToken upgradedToken = upgradedTokenCache.get(cookieTokens[0]);
            if (upgradedToken != null) {
                login = upgradedToken.getUserLoginIfValid(cookieTokens);
                LOG.debug("Detected previously upgraded login token for user '{}'", login);
            }

            if (login == null) {
                final PersistentToken token = getPersistentToken(cookieTokens);
                login = token.getUser().getLogin();

                // Token also matches, so login is valid. Update the token value, keeping the *same* series number.
                LOG.debug("Refreshing persistent login token for user '{}', series '{}'", login, token.getSeries());
                token.setTokenDate(new Date());
                token.setTokenValue(generateTokenData());
                token.setIpAddress(request.getRemoteAddr());
                token.setUserAgent(request.getHeader("User-Agent"));
                try {
                    persistentTokenRepository.saveAndFlush(token);
                } catch (final DataAccessException e) {
                    LOG.error("Failed to update token: ", e);
                    throw new RememberMeAuthenticationException("Autologin failed due to data access problem", e);
                }
                addCookie(token, request, response);
                upgradedTokenCache.put(cookieTokens[0], new UpgradedRememberMeToken(cookieTokens, login));
            }
            return getUserDetailsService().loadUserByUsername(login);
        }
    }

    @Override
    protected void onLoginSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication successfulAuthentication) {
        final String login = successfulAuthentication.getName();

        LOG.trace("Creating new persistent login for user {}", login);
        final User user = userRepository.findByLogin(login);

        final PersistentToken token = new PersistentToken();
        token.setSeries(generateSeriesData());
        token.setUser(user);
        token.setTokenValue(generateTokenData());
        token.setTokenDate(new Date());
        token.setIpAddress(request.getRemoteAddr());
        token.setUserAgent(request.getHeader("User-Agent"));
        try {
            persistentTokenRepository.saveAndFlush(token);
            addCookie(token, request, response);
        } catch (final DataAccessException e) {
            LOG.error("Failed to save persistent token ", e);
        }
    }

    /**
     * When logout occurs, only invalidate the current token, and not all user sessions.
     * <p/>
     * The standard Spring Security implementations are too basic: they invalidate all tokens for the current user, so when he logs out from one
     * browser, all his other sessions are destroyed.
     */
    @Override
    public void logout(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
        final String rememberMeCookie = extractRememberMeCookie(request);
        if (rememberMeCookie != null && !rememberMeCookie.isEmpty()) {
            try {
                final String[] cookieTokens = decodeCookie(rememberMeCookie);
                final PersistentToken token = getPersistentToken(cookieTokens);
                persistentTokenRepository.delete(token);
            } catch (final InvalidCookieException ice) {
                LOG.trace("Invalid cookie, no persistent token could be deleted : {}", ice.getMessage());
            } catch (final RememberMeAuthenticationException rmae) {
                LOG.debug("Persistent token not found or invalid : {}", rmae.getMessage());
            }
        }
        super.logout(request, response, authentication);
    }

    /**
     * Validate the token and return it.
     */
    private PersistentToken getPersistentToken(final String[] cookieTokens) {
        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain 2 tokens, but contained '" + Arrays.asList(cookieTokens)
                                             + "'");
        }

        final String presentedSeries = cookieTokens[0];
        final String presentedToken = cookieTokens[1];

        final Optional<PersistentToken> oToken = persistentTokenRepository.findById(presentedSeries);

        if (!oToken.isPresent()) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }

        final PersistentToken token = oToken.get();
        if (token.getUser() == null) {
            throw new RememberMeAuthenticationException("No user in this token");
        }

        // We have a match for this user/series combination
        LOG.trace("presentedToken={} / tokenValue={}", presentedToken, token.getTokenValue());
        if (!presentedToken.equals(token.getTokenValue())) {
            // Token doesn't match series value. Delete this session and throw an exception.
            persistentTokenRepository.delete(token);
            throw new RememberMeAuthenticationException("Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack.");
        }

        if (DateUtils.convertToLocalDate(token.getTokenDate()).plusDays(TOKEN_VALIDITY_DAYS).isBefore(LocalDate.now())) {
            persistentTokenRepository.delete(token);
            throw new RememberMeAuthenticationException("Remember-me login has expired");
        }
        return token;
    }

    private String generateSeriesData() {
        final byte[] newSeries = new byte[DEFAULT_SERIES_LENGTH];
        random.nextBytes(newSeries);
        return new String(Base64.getEncoder().encode(newSeries));
    }

    private String generateTokenData() {
        final byte[] newToken = new byte[DEFAULT_TOKEN_LENGTH];
        random.nextBytes(newToken);
        return new String(Base64.getEncoder().encode(newToken));
    }

    private void addCookie(final PersistentToken token, final HttpServletRequest request, final HttpServletResponse response) {
        setCookie(new String[] {token.getSeries(),
                                token.getTokenValue()}, TOKEN_VALIDITY_DAYS * 86400, request, response);
    }

    private static class UpgradedRememberMeToken implements Serializable {

        private static final long serialVersionUID = 1L;

        private final String[] upgradedToken;

        private final String userLogin;

        UpgradedRememberMeToken(final String[] upgradedToken, final String userLogin) {
            this.upgradedToken = upgradedToken;
            this.userLogin = userLogin;
        }

        String getUserLoginIfValid(final String[] currentToken) {
            if (currentToken[0].equals(this.upgradedToken[0]) && currentToken[1].equals(this.upgradedToken[1])) {
                return this.userLogin;
            }
            return null;
        }
    }
}
