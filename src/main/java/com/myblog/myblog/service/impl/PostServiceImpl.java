package com.myblog.myblog.service.impl;

import com.myblog.myblog.entity.Post;
import com.myblog.myblog.exception.ResourceNotFoundException;
import com.myblog.myblog.payload.PostDto;
import com.myblog.myblog.repository.PostRepository;
import com.myblog.myblog.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private ModelMapper mapper;

  //  @Autowired
    public PostServiceImpl(PostRepository postRepository, ModelMapper mapper) {
        this.postRepository = postRepository;
        this.mapper = mapper;
    }
@Override
public PostDto createPost(PostDto postDto){
        Post post = mapToEntity(postDto);
        Post newPost = postRepository.save(post);
        PostDto dto=mapToDto(newPost);
        return dto;
}




    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(pageNo,pageSize, sort);
        Page<Post> content = postRepository.findAll(pageable);
        List<Post> posts = content.getContent();

        List<PostDto> dto = posts.stream().map(post-> mapToDto(post)).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(dto);
        postResponse.setPageNo(content.getNumber());
        postResponse.setPageSize(content.getSize());
        postResponse.setTotalPages(content.getTotalPages());
        postResponse.setTotalElements((int)content.getTotalElements());
        postResponse.setLast(content.isLast());

        return postResponse;
    }

    @Override
    public PostDto getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("Post","Id",id)
        );
        return mapToDto(post);

    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {
        Post post = postRepository.findById(id).orElseThrow(

                () -> new ResourceNotFoundException("Post", "Id", id)
        );
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());

        Post updatedPost = postRepository.save(post);

        return mapToDto(updatedPost);
    }

    @Override
    public void deletePostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("post", "Id", id)
        );
        postRepository.deleteById(id);
    }


    PostDto mapToDto(Post post){
        PostDto postDto = mapper.map(post, PostDto.class);
        return postDto;
                }

         Post mapToEntity(PostDto dto){
   Post post = mapper.map(dto, Post.class);
   return post;
         }




}
