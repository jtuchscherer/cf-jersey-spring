package com.pivotallabs.orm;

import com.sun.jersey.api.ConflictException;
import com.sun.jersey.api.NotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.hibernate.criterion.Projections.rowCount;
import static org.hibernate.criterion.Restrictions.eq;

@Repository
@Transactional
public class UserDao {
    private
    @Resource
    SessionFactory sessionFactory;

    public void create(User user) {
        if (numberOfUsersWithName(user.getName()) != 0) {
            handleUserAlreadyExistsError(user);
        }
        if (isUserRoleInvalid(user)) {
            handleInvalidUserRole(user);
        } else {
            getCurrentSession().save(user);
        }
    }

    @SuppressWarnings("unchecked")
    public List<User> getAll() {
        return getCurrentSession().createCriteria(User.class).list();
    }

    public void deleteUser(User userToDelete) {
        checkIfUserExists(userToDelete);
        getCurrentSession().delete(userToDelete);
    }

    public void updateUser(User userToUpdate) {
        checkIfUserExists(userToUpdate);
        if (isUserRoleInvalid(userToUpdate)) {
            handleInvalidUserRole(userToUpdate);
        } else {
            getCurrentSession().update(userToUpdate);
        }
    }

    private void checkIfUserExists(User userToUpdate) {
        if (numberOfUsersWithName(userToUpdate.getName()) == 0) {
            throw new NotFoundException(format("User with name %s is not found", userToUpdate.getName()));
        }
    }

    public User findUser(String name) {
        return (User) getCurrentSession().get(User.class, name);
    }

    private int numberOfUsersWithName(String username) {
        return ((Number) getCurrentSession().createCriteria(User.class).add(eq("name", username)).setProjection(rowCount()).uniqueResult()).intValue();
    }

    private boolean isUserRoleInvalid(User userToUpdate) {
        return userToUpdate.getRoles().isEmpty() || userToUpdate.getRoles().contains(null);
    }

    private void handleInvalidUserRole(User userToUpdate) {
        Map<String, String> constraintViolations = new HashMap<>();
        constraintViolations.put("role", "validation_error.user.role");
        throw new UserValidationException(userToUpdate, constraintViolations);
    }

    private void handleUserAlreadyExistsError(User user) {
        throw new ConflictException(format("User with name %s already exists", user.getName()));
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}
