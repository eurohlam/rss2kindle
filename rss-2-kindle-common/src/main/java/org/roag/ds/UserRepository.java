package org.roag.ds;

import org.roag.ds.OperationResult;
import org.roag.model.User;

import java.util.List;
import java.util.Map;

/**
 * Created by eurohlam on 26/10/2017.
 */
public interface UserRepository {

    public User getUser(String username) throws Exception;

    public OperationResult addUser(User user) throws Exception;

    public OperationResult updateUser(User user) throws Exception;

    public OperationResult removeUser(String username) throws Exception;

    public OperationResult lockUser(String username) throws Exception;

    public OperationResult unlockUser(String username) throws Exception;

    public List<User> findAll() throws Exception;

    public List<User> findAll(Map condition) throws Exception;

}
