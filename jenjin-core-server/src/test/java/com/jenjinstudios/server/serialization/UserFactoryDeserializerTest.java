package com.jenjinstudios.server.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jenjinstudios.server.authentication.BasicUser;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.authentication.UserFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test the UserFactoryDeserializer class.
 *
 * @author Caleb Brinkman
 */
public class UserFactoryDeserializerTest
{
    /**
     * Test the deserialize functionality.
     *
     * @throws Exception If there's an unexpected exception.
     */
    @Test
    public void testDeserialize() throws Exception {
        String testJson = "{\n" +
              "\"class\":\"com.jenjinstudios.server.authentication.BasicUserFactory\"\n" +
              "}\n";
        UserFactoryDeserializer<BasicUser> deserializer = new UserFactoryDeserializer<>();
        Gson gson = new GsonBuilder()
              .registerTypeAdapter(UserFactory.class, deserializer)
              .create();
        UserFactory factory = gson.fromJson(testJson, UserFactory.class);
        User bob = factory.createUser("Bob");

        assertEquals(bob.getUsername(), "Bob", "Username should be bob");
        assertTrue(bob instanceof BasicUser, "Should be a basic user");
    }
}