package com.pivotallabs.web;

import com.google.gson.Gson;
import com.pivotallabs.orm.Role;
import com.pivotallabs.orm.User;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class UserResourceIntegrationTest {

    private static final String USERS_ENDPOINT = "http://localhost:8888/rest/users";
    private HttpClient httpClient;

    @BeforeMethod
    public void closeConnection() {
        httpClient = HttpClientBuilder.create().build();
    }

    @Test
    public void createAUser() throws IOException {
        HttpPost httpPost = new HttpPost(USERS_ENDPOINT + "/withFormValues");

        ArrayList<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("name", "adam"));
        postParameters.add(new BasicNameValuePair("email", "adam@admin.com"));
        postParameters.add(new BasicNameValuePair("role", "<role><name>admin</name></role>"));

        httpPost.setEntity(new UrlEncodedFormEntity(postParameters));

        HttpResponse response = httpClient.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(IOUtils.toString(response.getEntity().getContent())).contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><user><email>adam@admin.com</email><name>adam</name><roles><name>admin</name></roles></user>");
    }

    @Test(dependsOnMethods = "createAUser")
    public void createAUserWithJSON() throws IOException {
        Gson gson= new Gson();
        User user = new User();
        user.setName("tom");
        user.setEmail("tom@admin.com");
        Role role = new Role();
        role.setName("admin");
        user.setRoles(Arrays.asList(role));

        HttpPost httpPost = new HttpPost(USERS_ENDPOINT);
        StringEntity params = new StringEntity(gson.toJson(user));

        httpPost.addHeader("content-type", "application/json");
        httpPost.addHeader("Accept", "application/json");
        httpPost.setEntity(params);

        HttpResponse response = httpClient.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(IOUtils.toString(response.getEntity().getContent())).contains("{\"name\":\"tom\",\"email\":\"tom@admin.com\",\"roles\":[{\"name\":\"admin\"}]}");
    }

    @Test(dependsOnMethods = "createAUserWithJSON")
    public void getAllUsersWithJson() throws IOException {
        HttpGet httpGet;
        httpGet = new HttpGet(USERS_ENDPOINT);
        httpGet.addHeader("Accept", "application/json");

        HttpResponse response = httpClient.execute(httpGet);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        HttpEntity responseEntity = response.getEntity();
        assertThat(IOUtils.toString(responseEntity.getContent())).contains("[{\"name\":\"adam\",\"email\":\"adam@admin.com\",\"roles\":[{\"name\":\"admin\"}]},{\"name\":\"tom\",\"email\":\"tom@admin.com\",\"roles\":[{\"name\":\"admin\"}]}]");
    }

    @Test(dependsOnMethods = "createAUserWithJSON")
    public void getAllUsers() throws IOException {
        HttpGet httpGet;
        httpGet = new HttpGet(USERS_ENDPOINT);

        HttpResponse response = httpClient.execute(httpGet);
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        HttpEntity responseEntity = response.getEntity();
        assertThat(IOUtils.toString(responseEntity.getContent())).contains("<users><user><email>adam@admin.com</email><name>adam</name><roles><name>admin</name></roles></user><user><email>tom@admin.com</email><name>tom</name><roles><name>admin</name></roles></user></users>");
    }

    @Test(dependsOnMethods = "getAllUsers")
    public void getOneUser() throws IOException {
        HttpGet httpGet;
        httpGet = new HttpGet(USERS_ENDPOINT + "/adam");

        HttpResponse response = httpClient.execute(httpGet);
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        HttpEntity responseEntity = response.getEntity();
        assertThat(IOUtils.toString(responseEntity.getContent())).contains("<user><email>adam@admin.com</email><name>adam</name><roles><name>admin</name></roles></user>");
    }

    @Test(dependsOnMethods = "getOneUser")
    public void findOneUser() throws IOException {
        HttpGet httpGet;
        httpGet = new HttpGet(USERS_ENDPOINT + "/search?name=tom");

        HttpResponse response = httpClient.execute(httpGet);
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        HttpEntity responseEntity = response.getEntity();
        assertThat(IOUtils.toString(responseEntity.getContent())).contains("<user><email>tom@admin.com</email><name>tom</name><roles><name>admin</name></roles></user>");
    }

    @Test(dependsOnMethods = "findOneUser")
    public void deleteOneUser() throws IOException {
        HttpDelete httpDelete;
        httpDelete = new HttpDelete(USERS_ENDPOINT + "/tom");

        HttpResponse response = httpClient.execute(httpDelete);
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet;
        httpGet = new HttpGet(USERS_ENDPOINT);

        response = httpClient.execute(httpGet);
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        HttpEntity responseEntity = response.getEntity();
        assertThat(IOUtils.toString(responseEntity.getContent())).doesNotContain("tom");
    }

    @Test(dependsOnMethods = "deleteOneUser")
    public void updateUser() throws IOException {
        HttpPut httpPut = new HttpPut(USERS_ENDPOINT + "/adam");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("email", "new@email.com"));
        params.add(new BasicNameValuePair("role", "<role><name>user</name></role>"));
        httpPut.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = httpClient.execute(httpPut);
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet;
        httpGet = new HttpGet(USERS_ENDPOINT + "/adam");

        response = httpClient.execute(httpGet);
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        HttpEntity responseEntity = response.getEntity();
        assertThat(IOUtils.toString(responseEntity.getContent())).contains("new@email.com");
    }
}
