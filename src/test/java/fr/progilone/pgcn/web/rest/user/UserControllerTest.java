package fr.progilone.pgcn.web.rest.user;

import fr.progilone.pgcn.domain.dto.user.RoleDTO;
import fr.progilone.pgcn.domain.dto.user.UserDTO;
import fr.progilone.pgcn.domain.user.Role;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.service.user.ui.UIUserService;
import fr.progilone.pgcn.util.TestUtil;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static fr.progilone.pgcn.util.SecurityRequestPostProcessors.*;
import static fr.progilone.pgcn.web.rest.user.security.AuthorizationConstants.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private static final String USER_ID = "90cdcf40-ff39-4d8d-aad5-249c29a94b3a";

    @Mock
    private AccessHelper accessHelper;
    @Mock
    private LibraryAccesssHelper libraryAccesssHelper;
    @Mock
    private UserService userService;
    @Mock
    private UIUserService uiUserService;

    private MockMvc restMockMvc;

    private final RequestPostProcessor role_admin = roles(USER_HAB0, USER_HAB2);
    private final RequestPostProcessor role_user = roles(USER_HAB6);

    @Before
    public void setUp() {
        final UserController controller = new UserController(userService, uiUserService, accessHelper, libraryAccesssHelper);
        this.restMockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        final CustomUserDetails customUserDetails = new CustomUserDetails(USER_ID, null, null, null, null, null, false, User.Category.OTHER);
        final TestingAuthenticationToken authenticationToken =
            new TestingAuthenticationToken(customUserDetails, "3b03c8c5-c552-450e-a91d-7bb850fd8186");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    public void testGetById() throws Exception {
        final User user = new User();
        user.setIdentifier("49521b8a-4f46-4724-831c-1d24d6deecc4");
        final UserDTO dto = new UserDTO();
        dto.setIdentifier(user.getIdentifier());

        when(accessHelper.checkUser(anyString())).thenReturn(false, true, false, true);
        when(uiUserService.getOne(anyString())).thenReturn(dto);

        // pas de droits HAB0, mauvais user
        this.restMockMvc.perform(get("/api/rest/user/{id}", user.getIdentifier()).accept(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(status().isForbidden());

        // pas de droits HAB0, bon user, check ko
        user.setIdentifier(USER_ID);
        this.restMockMvc.perform(get("/api/rest/user/{id}", user.getIdentifier()).accept(TestUtil.APPLICATION_JSON_UTF8).with(role_user))
                        .andExpect(status().isForbidden());

        // pas de droits HAB0, bon user, check ok
        user.setIdentifier(USER_ID);
        dto.setIdentifier(user.getIdentifier());
        this.restMockMvc.perform(get("/api/rest/user/{id}", user.getIdentifier()).accept(TestUtil.APPLICATION_JSON_UTF8).with(role_user))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$.identifier").value(user.getIdentifier()));

        // droits HAB0, check ko
        user.setIdentifier("49521b8a-4f46-4724-831c-1d24d6deecc4");
        dto.setIdentifier(user.getIdentifier());
        this.restMockMvc.perform(get("/api/rest/user/{id}", user.getIdentifier()).accept(TestUtil.APPLICATION_JSON_UTF8).with(role_admin))
                        .andExpect(status().isForbidden());

        // droits HAB0, check ok
        this.restMockMvc.perform(get("/api/rest/user/{id}", user.getIdentifier()).accept(TestUtil.APPLICATION_JSON_UTF8).with(role_admin))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$.identifier").value(user.getIdentifier()));
    }

    @Test
    public void testUpdate() throws Exception {
        final UserDTO user = new UserDTO();
        user.setIdentifier("49521b8a-4f46-4724-831c-1d24d6deecc4");
        final User dbUser = new User();
        dbUser.setIdentifier(user.getIdentifier());
        // roles
        final RoleDTO role1 = new RoleDTO();
        role1.setIdentifier("353bd4f7-4bb5-4303-a110-ee3694cb4aaa");
        final Role role2 = new Role();
        role1.setIdentifier("89e4d093-d2e0-4a5b-b7c1-63414f5ce00f");

        when(accessHelper.checkUser(user.getIdentifier())).thenReturn(false, true);
        when(uiUserService.update(any(UserDTO.class))).then(new ReturnsArgumentAt(0));
        when(userService.getOne(anyString())).thenReturn(dbUser);

        // USER_HAB2 ko
        this.restMockMvc.perform(post("/api/rest/user/{id}", user.getIdentifier()).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                                  .content(TestUtil.convertObjectToJsonBytes(user))
                                                                                  .with(role_admin))
                        .andExpect(status().isForbidden());

        // USER_HAB2 ok
        this.restMockMvc.perform(post("/api/rest/user/{id}", user.getIdentifier()).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                                  .content(TestUtil.convertObjectToJsonBytes(user))
                                                                                  .with(role_admin))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$.identifier").value(user.getIdentifier()));

        // USER_HAB6 ko -> autre user
        this.restMockMvc.perform(post("/api/rest/user/{id}", user.getIdentifier()).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                                  .content(TestUtil.convertObjectToJsonBytes(user))
                                                                                  .with(role_user))
                        .andExpect(status().isForbidden());

        // USER_HAB6 ko -> profil
        user.setIdentifier(USER_ID);
        user.setRole(role1);
        dbUser.setRole(role2);

        this.restMockMvc.perform(post("/api/rest/user/{id}", user.getIdentifier()).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                                  .content(TestUtil.convertObjectToJsonBytes(user))
                                                                                  .with(role_user))
                        .andExpect(status().isForbidden());

        // USER_HAB6 ok
        role2.setIdentifier(role1.getIdentifier());

        this.restMockMvc.perform(post("/api/rest/user/{id}", user.getIdentifier()).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                                  .content(TestUtil.convertObjectToJsonBytes(user))
                                                                                  .with(role_user))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$.identifier").value(user.getIdentifier()));
    }
}
