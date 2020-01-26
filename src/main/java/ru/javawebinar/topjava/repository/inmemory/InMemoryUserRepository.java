package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private Map<Integer, User> repository = new ConcurrentHashMap<>();
    AtomicInteger count = new AtomicInteger(0);

    @Override
    public boolean delete(int id) {
        log.info("delete {}", id);
        return (repository.remove(id) != null);
    }

    @Override
    public User save(User user) {
        log.info("save {}", user);
        if (user.isNew()) {
            user.setId(count.incrementAndGet());
            repository.put(user.getId(), user);
            return user;
        }
        return repository.computeIfPresent(user.getId(), (k, ov) -> user);
    }

    @Override
    public User get(int id) {
        log.info("get {}", id);
        return repository.get(id);
    }

    @Override
    public List<User> getAll() {
        log.info("getAll");
        List<User> result = new ArrayList<>(repository.values());
        result.sort((u1, u2) -> (u1.getName().compareTo(u2.getName())) != 0 ?
                u1.getName().compareTo(u2.getName()) :
                u1.getEmail().compareTo(u2.getEmail()));
        return result;
    }

    @Override
    public User getByEmail(String email) {
        log.info("getByEmail {}", email);
        return (repository.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst()).orElse(null);
    }

    public static void main(String[] args) {
        UserRepository userRepository = new InMemoryUserRepository();
        User user1 = new User(null, "AAA", "aa@some.net", "ewfwef", Role.ROLE_USER);
        User user2 = new User(null, "CCC", "bb@some.net", "ewfwef", Role.ROLE_USER);
        User user3 = new User(null, "BBB", "cc@some.net", "ewfwef", Role.ROLE_USER);
        User user4 = new User(null, "AAA", "ac@some.net", "ewfwef", Role.ROLE_USER);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);


        userRepository.getAll().forEach(System.out::println);
        System.out.println("************");
        System.out.println(userRepository.get(3));
        System.out.println("************");
        System.out.println(userRepository.getByEmail("cc@some.net"));
        System.out.println("*************");
        System.out.println("updated user 3, email should be qwerty@inter.net");
        user3.setEmail("qwerty@inter.net");
        userRepository.save(user3);
        System.out.println(userRepository.get(user3.getId()));
        System.out.println("************");
        User fakeUser = new User(1000, "test error", "", "", Role.ROLE_USER);
        userRepository.save(fakeUser);
        System.out.println(userRepository.get(fakeUser.getId()));
    }
}
