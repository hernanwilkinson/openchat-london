package org.openchat.api;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openchat.domain.posts.InappropriateLanguageException;
import org.openchat.domain.posts.Post;
import org.openchat.domain.posts.PostService;
import org.openchat.infrastructure.builders.PostBuilder;
import spark.Request;
import spark.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PostsAPIShould {

    private static final String POST_ID = UUID.randomUUID().toString();
    private static final String USER_ID = UUID.randomUUID().toString();
    private static final String POST_TEXT = "Some text";
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2018, 1, 10, 14, 30, 0);

    private static final Post POST = new PostBuilder().withPostId(POST_ID).withUserId(USER_ID).withText(POST_TEXT).withDateTime(DATE_TIME).build();
    private static final List<Post> POSTS = asList(POST);

    @Mock Request request;
    @Mock Response response;
    @Mock Request likeRequest;
    @Mock Response likeResponse;

    @Mock PostService postService;

    private PostsAPI postsAPI;

    @Before
    public void initialise() throws InappropriateLanguageException {
        postsAPI = new PostsAPI(postService);
        given(request.params("userId")).willReturn(USER_ID);
        given(request.body()).willReturn(jsonContaining(POST_TEXT));
        given(postService.createPost(USER_ID, POST_TEXT)).willReturn(POST);
    }

    @Test public void
    create_a_post() throws InappropriateLanguageException {
        postsAPI.createPost(request, response);

        verify(postService).createPost(USER_ID, POST_TEXT);
    }

    @Test public void
    return_a_json_representing_a_newly_created_post() {
        String result = postsAPI.createPost(request, response);

        verify(response).status(201);
        verify(response).type("application/json");
        assertThat(result).isEqualTo(jsonContaining(POST));
    }

    @Test public void
    return_error_when_creating_a_post_with_inappropriate_language() throws InappropriateLanguageException {
        given(postService.createPost(USER_ID, POST_TEXT)).willThrow(InappropriateLanguageException.class);

        String result = postsAPI.createPost(request, response);

        verify(response).status(400);
        assertThat(result).isEqualTo("Post contains inappropriate language.");
    }

    @Test public void
    return_a_json_containing_posts_from_a_given_user() {
        given(request.params("userId")).willReturn(USER_ID);
        given(postService.postsBy(USER_ID)).willReturn(POSTS);

        String result = postsAPI.postsByUser(request, response);

        verify(response).status(200);
        verify(response).type("application/json");
        assertThat(result).isEqualTo(jsonContaining(POSTS));
    }
    @Test public void
    can_like_a_post() throws InappropriateLanguageException {

        postsAPI.createPost(request, response);
        given(likeRequest.params("publicationId")).willReturn(POST_ID);
        given(likeRequest.body()).willReturn(new JsonObject()
                .add("userId",USER_ID).toString());

        postsAPI.likePost(likeRequest,likeResponse);

        verify(postService).likePost(POST_ID,USER_ID);
    }
    private String jsonContaining(List<Post> posts) {
        JsonArray json = new JsonArray();
        posts.forEach(post -> json.add(jsonObjectFor(post)));
        return json.toString();
    }

    private String jsonContaining(Post post) {
        return jsonObjectFor(post).toString();
    }

    private JsonObject jsonObjectFor(Post post) {
        return new JsonObject()
                        .add("postId", post.postId())
                        .add("userId", post.userId())
                        .add("text", post.text())
                        .add("dateTime", "2018-01-10T14:30:00Z");
    }

    private String jsonContaining(String text) {
        return new JsonObject().add("text", text).toString();
    }

}