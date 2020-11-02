package org.openchat;

import org.openchat.api.*;
import org.openchat.domain.posts.*;
import org.openchat.domain.users.IdGenerator;
import org.openchat.domain.users.UserRepository;
import org.openchat.domain.users.UserService;
import spark.Request;
import spark.Response;

import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.post;

public class Routes {

    private UsersAPI usersAPI;
    private LoginAPI loginAPI;
    private PostsAPI postsAPI;
    private FollowingAPI followingAPI;
    private WallAPI wallAPI;

    public void create() {
        createAPIs();
        swaggerRoutes();
        openchatRoutes();
    }

    private void createAPIs() {
        IdGenerator idGenerator = new IdGenerator();

        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(idGenerator, userRepository);

        Clock clock = new Clock();
        PostRepository postRepository = new PostRepository();
        LanguageService languageService = new LanguageService();
        PostService postService = new PostService(languageService, idGenerator, clock, postRepository);
        WallService wallService = new WallService(userService, postRepository);

        usersAPI = new UsersAPI(userService);
        loginAPI = new LoginAPI(userRepository);
        postsAPI = new PostsAPI(postService);
        followingAPI = new FollowingAPI(userService);
        wallAPI = new WallAPI(wallService);
    }

    private void openchatRoutes() {
        get("status", (req, res) -> "OpenChat: OK!");
        post("users", (req, res) -> usersAPI.createUser(req, res));
        get("users", (req, res) -> usersAPI.allUsers(req, res));
        post("login", (req, res) -> loginAPI.login(req, res));
        post("users/:userId/timeline", (req, res) -> postsAPI.createPost(req, res));
        get("users/:userId/timeline", (req, res) -> postsAPI.postsByUser(req, res));
        post("followings", (req, res) -> followingAPI.createFollowing(req, res));
        get("followings/:followerId/followees", (req, res) -> followingAPI.getFollowees(req, res));
        get("users/:userId/wall", (req, res) -> wallAPI.wallByUser(req, res));
        post("publications/:publicationId/like", (req, res) -> likePublication(req,res));
    }

    private String likePublication(Request request, Response response) {
        throw new RuntimeException("Should implement");
    }

    private void swaggerRoutes() {
        options("users", (req, res) -> "OK");
        options("login", (req, res) -> "OK");
        options("users/:userId/timeline", (req, res) -> "OK");
        options("followings", (req, res) -> "OK");
        options("followings/:userId/followees", (req, res) -> "OK");
        options("users/:userId/wall", (req, res) -> "OK");
    }
}
