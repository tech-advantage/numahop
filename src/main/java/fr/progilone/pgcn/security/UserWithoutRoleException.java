package fr.progilone.pgcn.security;

import org.springframework.security.core.AuthenticationException;

/**
 * This exception is thrown in case of a user without any role is trying to authenticate.
 */
public class UserWithoutRoleException extends AuthenticationException {

    public UserWithoutRoleException(String message) {
        super(message);
    }

    public UserWithoutRoleException(String message, Throwable t) {
        super(message, t);
    }
}
