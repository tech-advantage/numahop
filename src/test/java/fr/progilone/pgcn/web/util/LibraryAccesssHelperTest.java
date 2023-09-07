package fr.progilone.pgcn.web.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import fr.progilone.pgcn.domain.user.Lang;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class LibraryAccesssHelperTest {

    public static final CustomUserDetails USER_TOTO = new CustomUserDetails("199c0268-1b1b-453d-9725-6d9747588477",
                                                                            "toto",
                                                                            "azerty",
                                                                            Lang.FR,
                                                                            "LIB-001",
                                                                            Collections.emptyList(),
                                                                            false,
                                                                            User.Category.OTHER);
    public static final CustomUserDetails USER_SUPER = new CustomUserDetails("9503d0dc-014b-413e-ab2f-3521572545fa",
                                                                             "superman",
                                                                             "azerty",
                                                                             Lang.FR,
                                                                             "LIB-001",
                                                                             Collections.emptyList(),
                                                                             true,
                                                                             User.Category.OTHER);

    @Mock
    private AccessHelper accessHelper;
    @Mock
    private HttpServletRequest request;

    private LibraryAccesssHelper accesssHelper;

    @BeforeEach
    public void setUp() {
        accesssHelper = new LibraryAccesssHelper(accessHelper);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void testCheckLibrary() {
        final String identifier = "71d96e0e-27e5-4ae6-a2fa-4ba95acfb30a";
        final String role_admin = "role_admin";

        when(accessHelper.checkCurrentUser(USER_TOTO)).thenReturn(Optional.of(false)).thenReturn(Optional.of(true)).thenReturn(Optional.empty());
        setUser(USER_TOTO);

        // pas connecté
        boolean actual = accesssHelper.checkLibrary(request, identifier, role_admin);
        assertFalse(actual);

        // super user
        actual = accesssHelper.checkLibrary(request, identifier, role_admin);
        assertTrue(actual);

        // user normal ko
        actual = accesssHelper.checkLibrary(request, identifier, role_admin);
        assertFalse(actual);

        // user normal ok
        actual = accesssHelper.checkLibrary(request, USER_TOTO.getLibraryId(), role_admin);
        assertTrue(actual);
    }

    private void setUser(final CustomUserDetails userDetails) {
        final TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(userDetails, "credentials");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
