package org.roag.ds;

import org.roag.ds.OperationResult;
import org.roag.model.Roles;
import org.roag.model.User;

import java.util.List;
import java.util.Map;

/**
 * Created by eurohlam on 26/10/2017.
 */
public interface UserRepository {

    User getUser(String username) throws Exception;

    OperationResult addUser(User user) throws Exception;

    OperationResult updateUser(User user) throws Exception;

    OperationResult removeUser(String username) throws Exception;

    OperationResult lockUser(String username) throws Exception;

    OperationResult unlockUser(String username) throws Exception;

    List<User> findAll() throws Exception;

    List<User> findAll(Map<String, String> conditions) throws Exception;

    OperationResult assignRole(String username, Roles role) throws Exception;

    OperationResult dismissRole(String username, Roles role) throws Exception;
}
