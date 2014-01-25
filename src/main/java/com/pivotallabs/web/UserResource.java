package com.pivotallabs.web;

import com.pivotallabs.orm.Role;
import com.pivotallabs.orm.User;
import com.pivotallabs.orm.UserDao;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@Controller
public class UserResource {

    @Resource
    public UserDao userDao;

    @POST
    public Response addUser(@FormParam("name") String name,
                            @FormParam("email") String email,
                            @FormParam("role") List<Role> roles) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRoles(roles);

        userDao.create(user);
        return Response.ok().entity(user).build();
    }

    @PUT
    @Path("{name}")
    public Response updateUser(@PathParam("name") String name,
                               @FormParam("email") String email,
                               @FormParam("role") List<Role> roles) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRoles(roles);

        userDao.updateUser(user);
        return Response.ok().entity(user).build();
    }

    @DELETE
    @Path("{name}")
    public Response deleteUser(@PathParam("name") String name) {
        User user = new User();
        user.setName(name);
        userDao.deleteUser(user);
        return Response.ok().entity(user).build();
    }

    @GET
    public Response getUsers() {
        List<User> users = userDao.getAll();
        GenericEntity<List<User>> listEntity = new GenericEntity<List<User>>(users) {
        };
        return Response.ok().entity(listEntity).build();
    }

    @GET
    @Path("{name}")
    public Response getUser(@PathParam("name") String name) {
        User user = userDao.findUser(name);
        return Response.ok().entity(user).build();
    }

    @GET
    @Path("search/")
    public Response findUser(@QueryParam("name") String name) {
        return getUser(name);
    }
}