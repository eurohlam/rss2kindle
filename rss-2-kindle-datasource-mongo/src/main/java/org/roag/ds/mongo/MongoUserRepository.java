package org.roag.ds.mongo;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.roag.ds.OperationResult;
import org.roag.ds.UserRepository;
import org.roag.model.Roles;
import org.roag.model.User;
import org.roag.model.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by eurohlam on 31/10/2017.
 */
public class MongoUserRepository implements UserRepository {
    private final Logger logger = LoggerFactory.getLogger(MongoUserRepository.class);

    private CamelContext context;
    private ProducerTemplate producerTemplate;
    private MongoHelper mongoHelper;

    private static final String MONGO_WRITE_RESULT = "WriteResult";

    public MongoUserRepository(MongoHelper mongoHelper, CamelContext context) {
        this.mongoHelper = mongoHelper;
        assert context != null;
        this.context = context;
        this.producerTemplate = context.createProducerTemplate();
    }

    public CamelContext getCamelContext() {
        return context;
    }

    public ProducerTemplate getProducerTemplate() {
        return producerTemplate;
    }

    public MongoHelper getMongoHelper() {
        return mongoHelper;
    }

    @Override
    public List<User> findAll() throws Exception {
        logger.debug("Fetch all users from Mongo ");
        return mongoHelper.getAllUsers(producerTemplate);
    }

    @Override
    public List<User> findAll(Map<String, String> conditions) throws Exception {
        logger.debug("Fetch all users from Mongo by condition {}", conditions);
        return mongoHelper.getUsers(producerTemplate, conditions);
    }

    @Override
    public User getUser(String username) throws Exception {
        logger.debug("Fetch user {} from Mongo", username);
        return mongoHelper.getUser(username, producerTemplate);
    }

    @Override
    public OperationResult addUser(User user) throws Exception {
        logger.debug("Add new user {}", user.getUsername());
        OperationResult r = mongoHelper.addUser(user, producerTemplate);
        logger.info("Added user {} with the result {}", user.getUsername(), r.toString().replaceFirst(MONGO_WRITE_RESULT, ""));
        return r;
    }

    @Override
    public OperationResult updateUser(User user) throws Exception {
        logger.debug("Update user {}", user.getUsername());
        OperationResult r = mongoHelper.updateUser(user, producerTemplate);
        logger.info("Updated user {} with the result {}", user.getUsername(), r.toString().replaceFirst(MONGO_WRITE_RESULT, ""));
        return r;
    }

    @Override
    public OperationResult removeUser(String username) throws Exception {
        logger.debug("Remove user {}", username);
        OperationResult r = mongoHelper.removeUser(username, producerTemplate);
        logger.info("Removed user {} with the result {}", username, r.toString().replaceFirst(MONGO_WRITE_RESULT, ""));
        return r;
    }

    @Override
    public OperationResult lockUser(String username) throws Exception {
        User user = getUser(username);
        user.setStatus(UserStatus.LOCKED.toString());
        logger.warn("Trying to lock user {}", username);
        return updateUser(user);
    }

    @Override
    public OperationResult unlockUser(String username) throws Exception {
        User user = getUser(username);
        user.setStatus(UserStatus.ACTIVE.toString());
        logger.warn("Trying to activate user {}", username);
        return updateUser(user);
    }

    @Override
    public OperationResult assignRole(String username, Roles role) throws Exception {
        User user = getUser(username);
        for (Roles r : user.getRoles())
            if (r == role)
                return OperationResult.DUPLICATED;

        user.getRoles().add(role);
        return updateUser(user);
    }

    @Override
    public OperationResult dismissRole(String username, Roles role) throws Exception {
        User user = getUser(username);
        for (Roles r : user.getRoles())
            if (r == role) {
                user.getRoles().remove(r);
                return updateUser(user);
            }
        return OperationResult.NOT_EXIST;
    }
}
