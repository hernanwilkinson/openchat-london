package org.openchat.domain.users;

import org.openchat.domain.posts.PostService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;

public class UserRepository {

    private List<User> users = new ArrayList<>();
    private List<Following> followings = new ArrayList<>();

    public void add(User user) {
        users.add(user);
    }

    public boolean isUsernameTaken(String username) {
        return users.stream()
                    .anyMatch(user -> user.username().equals(username));
    }

    public Optional<User> userFor(UserCredentials userCredentials) {
        return users.stream()
                    .filter(userCredentials::matches)
                    .findFirst();
    }

    public List<User> all() {
        return unmodifiableList(users);
    }

    public void add(Following following) {
        followings.add(following);
    }

    public boolean hasFollowing(Following following) {
        return followings.stream()
                        .anyMatch(f -> f.equals(following));
    }

    public List<User> followeesBy(String followerId) {
        return followings.stream()
                         .filter(following -> following.followerId().equals(followerId))
                         .map(following -> following.followeeId())
                         .map(followeeId -> userBy(followeeId).get())
                         .collect(Collectors.toList());
    }

    public Optional<User> userBy(String userId) {
        return users.stream()
                    .filter(user -> user.id().equals(userId))
                    .findFirst();
    }

    public User userByOrThrow(String userId) throws InvalidUser {
        return userBy(userId).orElseThrow(() -> new InvalidUser());
    }
}
