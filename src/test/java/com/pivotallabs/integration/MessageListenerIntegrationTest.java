package com.pivotallabs.integration;

import com.pivotallabs.config.ORMConfig;
import com.pivotallabs.config.OfflineConfig;
import com.pivotallabs.config.RabbitConfig;
import com.pivotallabs.config.SpringConfig;
import com.pivotallabs.orm.Role;
import com.pivotallabs.orm.User;
import com.pivotallabs.orm.UserDao;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

import static com.pivotallabs.config.RabbitConfig.QUEUE_NAME;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

@ContextConfiguration(classes = {OfflineConfig.class, RabbitConfig.class, SpringConfig.class, ORMConfig.class})
public class MessageListenerIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private RabbitTemplate template;

    @Resource
    private UserDao userDao;

    @AfterMethod
    private void cleanUpQueue() {
        template.receive(QUEUE_NAME);
    }

    @Test
    public void createAUser() throws IOException, InterruptedException {
        User adam = new User();
        adam.setName("adam");
        adam.setEmail("adam@email.com");
        adam.setRoles(asList(new Role()));
        template.convertAndSend(QUEUE_NAME, adam);

        List<User> users = userDao.getAll();
        assertThat(users).hasSize(1);
        User user = users.get(0);
        assertThat(user.getName()).isEqualTo("adam");
    }
}
