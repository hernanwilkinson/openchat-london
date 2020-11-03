package org.openchat.domain.posts;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openchat.domain.users.IdGenerator;
import org.openchat.domain.users.InvalidUser;
import org.openchat.infrastructure.builders.PostBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceShould {

    private static final String POSTID = UUID.randomUUID().toString();
    private static final String USER_ID = UUID.randomUUID().toString();
    private static final String TEXT = "text";
    private static final LocalDateTime DATE_TIME = LocalDateTime.now();

    private static final Post NEW_POST = new PostBuilder().withPostId(POSTID).withUserId(USER_ID).withText("text").withDateTime(DATE_TIME).build();
    private static final List<Post> POSTS = asList(NEW_POST);

    @Mock IdGenerator idGenerator;
    @Mock Clock clock;
    @Mock PostRepository postRepository;
    @Mock LanguageService languageService;

    PostService service;

    @Before
    public void initialise() {
        service = new PostService(languageService, idGenerator, clock, postRepository);
    }

    @Test public void
    create_post() throws InappropriateLanguageException {
        given(idGenerator.next()).willReturn(POSTID);
        given(clock.now()).willReturn(DATE_TIME);

        Post post = service.createPost(USER_ID, TEXT);

        verify(postRepository).add(NEW_POST);
        assertThat(post).isEqualTo(NEW_POST);
    }

    @Test(expected = InappropriateLanguageException.class) public void
    throw_exception_when_creating_post_with_inappropriate_language() throws InappropriateLanguageException {
        given(languageService.isInappropriate(TEXT)).willReturn(true);

        service.createPost(USER_ID, TEXT);
    }
    
    @Test public void
    return_posts_for_a_given_user() {
        given(postRepository.postsBy(USER_ID)).willReturn(POSTS);

        List<Post> result = service.postsBy(USER_ID);

        assertThat(result).isEqualTo(POSTS);
    }
    @Test public void
    like_post() throws InvalidPostException, InvalidUser {
        Post NEW_POST = new PostBuilder().withPostId(POSTID).withUserId(USER_ID).withText("text").withDateTime(DATE_TIME).build();
        given(postRepository.postIdentifiedAs(POSTID)).willReturn(NEW_POST);

        int likes = service.likePost(POSTID,USER_ID);

        assertThat(likes).isEqualTo(1);
    }
    @Test public void
    different_users_likes_do_count() throws InvalidPostException, InvalidUser {
        Post NEW_POST = new PostBuilder().withPostId(POSTID).withUserId(USER_ID).withText("text").withDateTime(DATE_TIME).build();
        final String anotherUserId = UUID.randomUUID().toString();

        given(postRepository.postIdentifiedAs(POSTID)).willReturn(NEW_POST);

        service.likePost(POSTID,USER_ID);
        int likes = service.likePost(POSTID, anotherUserId);

        assertThat(likes).isEqualTo(2);
    }
    @Test public void
    same_user_likes_do_not_count() throws InvalidPostException, InvalidUser {
        Post NEW_POST = new PostBuilder().withPostId(POSTID).withUserId(USER_ID).withText("text").withDateTime(DATE_TIME).build();

        given(postRepository.postIdentifiedAs(POSTID)).willReturn(NEW_POST);

        service.likePost(POSTID,USER_ID);
        int likes = service.likePost(POSTID, USER_ID);

        assertThat(likes).isEqualTo(1);
    }
}