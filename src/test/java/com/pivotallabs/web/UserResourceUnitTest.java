package com.pivotallabs.web;

import com.pivotallabs.orm.Role;
import com.pivotallabs.orm.User;
import com.pivotallabs.orm.UserDao;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserResourceUnitTest {

    UserResource userResource;
    UserDao userDao;

    @BeforeMethod
    public void setup() {
        userDao = mock(UserDao.class);
        userResource = new UserResource();
        ReflectionTestUtils.setField(userResource, "userDao", userDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getUsers_ReturnsA200AndTheUsersAsPayload() {
        when(userDao.getAll()).thenReturn(asList(new User(), new User()));
        Response response = userResource.getUsers();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isInstanceOf(GenericEntity.class);
        List<User> users = ((GenericEntity<List<User>>) response.getEntity()).getEntity();
        assertThat(users).hasSize(2);
    }

    @Test
    public void getUser_ReturnsA200AndTheUserAsPayload() {
        User adam = new User();
        adam.setName("adam");
        when(userDao.findUser("adam")).thenReturn(adam);
        Response response = userResource.getUser("adam");
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isInstanceOf(User.class);
        User user = (User) response.getEntity();
        assertThat(user.getName()).isEqualTo("adam");
    }

    @Test
    public void findUser_ReturnsA200AndTheUserAsPayload() {
        User adam = new User();
        adam.setName("adam");
        when(userDao.findUser("adam")).thenReturn(adam);
        Response response = userResource.findUser("adam");
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isInstanceOf(User.class);
        User user = (User) response.getEntity();
        assertThat(user.getName()).isEqualTo("adam");
    }

    @Test
    public void addUser_createsTheUser() {
        Role adminRole = new Role();
        adminRole.setName("admin");
        userResource.addUser("adam", "adam@email.com", asList(adminRole));
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).create(userArgumentCaptor.capture());
        User user = userArgumentCaptor.getValue();
        assertThat(user.getName()).isEqualTo("adam");
        assertThat(user.getEmail()).isEqualTo("adam@email.com");
        assertThat(user.getRoles()).hasSize(1);
        Role role = user.getRoles().get(0);
        assertThat(role.getName()).isEqualTo("admin");
    }

    @Test
    public void addUser_ReturnsA200WithTheUserAsPayload() {
        Response response = userResource.addUser("adam", "adam@email.com", asList(new Role()));
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isInstanceOf(User.class);
        User user = (User) response.getEntity();
        assertThat(user.getName()).isEqualTo("adam");
        assertThat(user.getEmail()).isEqualTo("adam@email.com");
        assertThat(user.getRoles()).hasSize(1);
    }

    @Test
    public void updateUser_updatesTheUser() {
        Role adminRole = new Role();
        adminRole.setName("admin");
        userResource.updateUser("adam", "adam@email.com", asList(adminRole));
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).updateUser(userArgumentCaptor.capture());
        User user = userArgumentCaptor.getValue();
        assertThat(user.getName()).isEqualTo("adam");
        assertThat(user.getEmail()).isEqualTo("adam@email.com");
        assertThat(user.getRoles()).hasSize(1);
        Role role = user.getRoles().get(0);
        assertThat(role.getName()).isEqualTo("admin");
    }

    @Test
    public void updateUser_ReturnsA200WithTheUserAsPayload() {
        Response response = userResource.updateUser("adam", "adam@email.com", asList(new Role()));
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isInstanceOf(User.class);
        User user = (User) response.getEntity();
        assertThat(user.getName()).isEqualTo("adam");
        assertThat(user.getEmail()).isEqualTo("adam@email.com");
        assertThat(user.getRoles()).hasSize(1);
    }

    @Test
    public void deleteUser_deletesTheUser() {
        userResource.deleteUser("adam");
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).deleteUser(userArgumentCaptor.capture());
        User user = userArgumentCaptor.getValue();
        assertThat(user.getName()).isEqualTo("adam");
    }

    @Test
    public void deleteUser_returns200AndTheDeletedUsername() {
        Response response = userResource.deleteUser("adam");
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isInstanceOf(User.class);
        User user = (User) response.getEntity();
        assertThat(user.getName()).isEqualTo("adam");

    }
}
