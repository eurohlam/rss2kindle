package org.roag.security;

import org.roag.ds.OperationResult;
import org.roag.model.User;
import org.roag.service.SubscriberFactory;
import org.springframework.stereotype.Service;

/**
 * Created by eurohlam on 30/10/2017.
 */
@Service
public class MemoryUserRepository implements UserRepository{


    @Override
    public User getUser(String username) throws Exception {
        SubscriberFactory factory = new SubscriberFactory();
        User user=factory.newUser(username, "test");
        return user;
    }

    @Override
    public OperationResult addUser(User user) throws Exception {
        return null;
    }

    @Override
    public OperationResult updateUser(User user) throws Exception {
        return null;
    }

    @Override
    public OperationResult removeUser(String username) throws Exception {
        return null;
    }

    @Override
    public OperationResult lockUser(String username) throws Exception {
        return null;
    }

    @Override
    public OperationResult unlockUser(String username) throws Exception {
        return null;
    }
}
