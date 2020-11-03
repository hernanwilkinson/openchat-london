package org.openchat.domain.posts;

import org.openchat.domain.users.IdGenerator;
import org.openchat.domain.users.InvalidUserException;
import org.openchat.domain.users.User;
import org.openchat.domain.users.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

public class PostService {
    private LanguageService languageService;
    private final IdGenerator idGenerator;
    private final Clock clock;
    private PostRepository repository;
    private final UserRepository userRepository;

    public PostService(LanguageService languageService,
                       IdGenerator idGenerator,
                       Clock clock,
                       PostRepository repository, UserRepository userRepository) {
        this.languageService = languageService;
        this.idGenerator = idGenerator;
        this.clock = clock;
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public Post createPost(String userId, String text) throws InappropriateLanguageException{
        validate(text);
        Post post = createNewPost(userId, text);
        repository.add(post);
        return post;
    }

    public List<Post> postsBy(String userId) {
        return repository.postsBy(userId);
    }

    private Post createNewPost(String userId, String text) {
        String postId = idGenerator.next();
        LocalDateTime now = clock.now();
        return new Post(postId, userId, text, now);
    }

    private void validate(String text) throws InappropriateLanguageException {
        if (languageService.isInappropriate(text)) {
            throw new InappropriateLanguageException();
        }
    }

    public int likePost(String postId, String userId) throws InvalidPostException, InvalidUserException {
        Post post = repository.postIdentifiedAs(postId);
        User liker = userRepository.userByOrThrow(userId);
        post.likedBy(liker);

        return post.likes();
    }
}
