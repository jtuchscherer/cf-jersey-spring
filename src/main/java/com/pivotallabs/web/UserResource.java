package com.pivotallabs.web;

import com.pivotallabs.orm.Role;
import com.pivotallabs.orm.User;
import com.pivotallabs.orm.UserDao;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@Controller
public class UserResource {

    @Autowired
    public RabbitTemplate rabbitTemplate;

    @Resource
    public UserDao userDao;

    @POST
    @Path("withFormValues")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response addUser(@FormParam("name") String name,
                            @FormParam("email") String email,
                            @FormParam("role") List<Role> roles) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRoles(roles);

        userDao.create(user);
        rabbitTemplate.convertAndSend("routingKey", "test");
        return Response.ok().entity(user).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addUser(User user) {


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
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getUsers() {
        List<User> users = userDao.getAll();
        GenericEntity<List<User>> listEntity = new GenericEntity<List<User>>(users) {};
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
