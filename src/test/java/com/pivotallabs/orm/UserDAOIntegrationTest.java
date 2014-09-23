package com.pivotallabs.orm;

import com.pivotallabs.web.SpringConfig;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;

@ContextConfiguration(classes={SpringConfig.class})
public class UserDAOIntegrationTest extends AbstractTransactionalTestNGSpringContextTests {

    @Resource
    private UserDao userDao;
    private Role adminRole = new Role();

    @Test
    public void getUsers_returnsAnEmptyListIfThereAreNoUsers() {
        List<User> users = userDao.getAll();
        assertThat(users).hasSize(0);
    }

    @Test(dependsOnMethods = "getUsers_returnsAnEmptyListIfThereAreNoUsers")
    public void createUser_cannotCreateUserWithoutARole() {
        User noRoleUser = validUser(1, null);
        try {
            userDao.create(noRoleUser);
            fail();
        } catch (UserValidationException exception) {
            assertThat(exception.getUser()).isEqualTo(noRoleUser);
            assertThat(exception.getConstraintViolations()).hasSize(1);
            String errorMessage = exception.getConstraintViolations().get("role");
            assertThat(errorMessage).isEqualTo("validation_error.user.role");
        }
    }

    @Test(dependsOnMethods = "createUser_cannotCreateUserWithoutARole")
    @Rollback(false)
    public void createUser_createsAValidUser() {
        adminRole.setName("adminRole");
        User adam = validUser(0, adminRole);
        userDao.create(adam);
    }

    @Test(dependsOnMethods = "createUser_createsAValidUser")
    public void createUser_cannotCreateAnotherUserWithAnExistingUsername() {
        Role someRole = new Role();
        someRole.setName("someRole");
        User adam = validUser(0, someRole);
        try {
            userDao.create(adam);
            fail();
        } catch (NotAcceptableException exception) {
            assertThat(exception.getMessage()).isEqualTo("User with name adam0 already exists");
        }
    }

    @Test(dependsOnMethods = "createUser_cannotCreateAnotherUserWithAnExistingUsername")
    public void getUsers_retrievesOneUser() {
        List<User> users = userDao.getAll();
        assertThat(users).hasSize(1);
        User user = users.get(0);
        assertThat(user.getName()).isEqualTo("adam0");
        assertThat(user.getEmail()).isEqualTo("adam@email.com");
        assertThat(user.getRoles()).hasSize(1);
        Role userRole = user.getRoles().get(0);
        assertThat(userRole.getName()).isEqualTo("adminRole");
    }

    @Test(dependsOnMethods = "getUsers_retrievesOneUser")
    public void getUsers_retrievesMoreUsers() {
        Role userRole = new Role();
        userRole.setName("userRole");
        for (int i = 1; i < 10; i++) {
            userDao.create(validUser(i, userRole));
        }
        List<User> users = userDao.getAll();
        assertThat(users).hasSize(10);
    }

    @Test(dependsOnMethods = "getUsers_retrievesMoreUsers")
    public void findUser_shouldFindUserByName() {
        User user = userDao.findUser("adam0");
        assertThat(user.getName()).isEqualTo("adam0");
        assertThat(user.getEmail()).isEqualTo("adam@email.com");
        assertThat(user.getRoles()).hasSize(1);
        Role userRole = user.getRoles().get(0);
        assertThat(userRole.getName()).isEqualTo("adminRole");
    }

    @Test(dependsOnMethods = "findUser_shouldFindUserByName")
    public void findUser_shouldReturnNullIfThereIsNoMatch() {
        User notFound = userDao.findUser("notFound");
        assertThat(notFound).isNull();
    }

    @Test(dependsOnMethods = "findUser_shouldReturnNullIfThereIsNoMatch")
    public void updateUser_shouldUpdateTheUser() {
        Role newRole = new Role();
        newRole.setName("newRole");
        User userToUpdate = validUser(0, newRole);
        userDao.updateUser(userToUpdate);
        User updatedUser = userDao.findUser("adam0");
        assertThat(updatedUser.getRoles()).contains(newRole);
    }

    @Test(dependsOnMethods = "updateUser_shouldUpdateTheUser")
    public void updateUser_shouldThrowIfUserNotFound() {
        Role newRole = new Role();
        newRole.setName("newRole");
        User userToUpdate = validUser(0, newRole);
        userToUpdate.setName("Does Not Exist");
        try {
            userDao.updateUser(userToUpdate);
            fail();
        } catch (NotFoundException exception) {
            assertThat(exception.getMessage()).isEqualTo("User with name Does Not Exist is not found");
        }
    }

    @Test(dependsOnMethods = "updateUser_shouldThrowIfUserNotFound")
    public void updateUser_shouldThrowIfUserRoleInvalid() {
        User userToUpdate = validUser(0, null);
        try {
            userDao.updateUser(userToUpdate);
            fail();
        } catch (UserValidationException exception) {
            assertThat(exception.getUser()).isEqualTo(userToUpdate);
            assertThat(exception.getConstraintViolations()).hasSize(1);
            String errorMessage = exception.getConstraintViolations().get("role");
            assertThat(errorMessage).isEqualTo("validation_error.user.role");
        }
    }

    @Test(dependsOnMethods = "updateUser_shouldThrowIfUserRoleInvalid")
    public void deleteUser_shouldThrowIfUserNotFound() {
        User userToDelete = validUser(0, adminRole);
        userToDelete.setName("Does Not Exist");
        try {
            userDao.deleteUser(userToDelete);
            fail();
        } catch (NotFoundException exception) {
            assertThat(exception.getMessage()).isEqualTo("User with name Does Not Exist is not found");
        }
    }

    @Test(dependsOnMethods = "deleteUser_shouldThrowIfUserNotFound")
    public void deleteUser_shouldDeleteTheUser() {
        User userToDelete = validUser(0, adminRole);
        userDao.deleteUser(userToDelete);
        assertThat(userDao.findUser("adam0")).isNull();
    }

    private User validUser(int index, Role role) {
        User adam = new User();
        adam.setName("adam" + index);
        adam.setEmail("adam@email.com");
        adam.setRoles(asList(role));
        return adam;
    }
}
