package org.roag.ds.impl;

import org.roag.ds.OperationResult;
import org.roag.ds.UserRepository;
import org.roag.model.User;
import org.roag.model.UserStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by eurohlam on 30/10/2017.
 */
@Service
public class MemoryUserRepository implements UserRepository {

    private Map<String, User> users;
    private static UserRepository repository;

    private MemoryUserRepository()
    {
        this.users = new ConcurrentHashMap<>();
    }

    public static UserRepository getInstance()
    {
        ReentrantReadWriteLock.WriteLock lock = new ReentrantReadWriteLock().writeLock();
        lock.lock();
        try
        {
            if (repository == null)
                repository = new MemoryUserRepository();
            return repository;
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public List<User> findAll() throws Exception {
        return new ArrayList<User>(users.values());
    }

    @Override
    public List<User> findAll(Map condition) throws Exception {
        return findAll(); //TODO: implement findAll with conditions
    }

    @Override
    public OperationResult addUser(User user) throws Exception
    {
        if (users.containsKey(user.getUsername()))
            throw new IllegalArgumentException("User " + user.getUsername() + " already exists");
        users.put(user.getUsername(), user);
        return OperationResult.SUCCESS;
    }

    @Override
    public User getUser(String username) throws Exception {
        return users.get(username);
    }

    @Override
    public OperationResult updateUser(User user) throws Exception {
        User u = users.replace(user.getUsername(), user);
        return u!=null? OperationResult.SUCCESS: OperationResult.FAILURE;
    }

    @Override
    public OperationResult removeUser(String username) throws Exception {
        User u = users.remove(username);
        return u!=null? OperationResult.SUCCESS: OperationResult.FAILURE;
    }

    @Override
    public OperationResult lockUser(String username) throws Exception {
        User user = getUser(username);
        user.setStatus(UserStatus.LOCKED.toString());
        return updateUser(user);
    }

    @Override
    public OperationResult unlockUser(String username) throws Exception {
        User user = getUser(username);
        user.setStatus(UserStatus.ACTIVE.toString());
        return updateUser(user);
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }
}
