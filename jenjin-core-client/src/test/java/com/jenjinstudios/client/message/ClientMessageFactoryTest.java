package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.io.Message;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ClientMessageFactoryTest
{
    @Test
    public void testGenerateLogoutRequest() {
        Message message = AuthClient.generateLogoutRequest();
        Assert.assertEquals(message.name, "LogoutRequest");
    }

    @Test
    public void testGenerateLoginRequest() {
        Message message = AuthClient.generateLoginRequest(new ClientUser("Foo", "Bar"));
        Assert.assertEquals(message.name, "LoginRequest");
        Assert.assertEquals(message.getArgument("username"), "Foo");
        Assert.assertEquals(message.getArgument("password"), "Bar");
    }
}
