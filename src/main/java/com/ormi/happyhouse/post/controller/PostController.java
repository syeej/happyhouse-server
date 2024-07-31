package com.ormi.happyhouse.post.controller;

import com.ormi.happyhouse.post.domain.Post;
import com.ormi.happyhouse.post.dto.PostDto;
import com.ormi.happyhouse.post.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/post")
public class PostController {

    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // Create: 게시글 생성
    @PostMapping
    public String savePost(@RequestBody PostDto postDto, Model model) {
        postService.savePost(postDto);
        return "redirect:/post";
    }

    // Read: 게시글 목록 조회
    @GetMapping
    public String showAllPost(
            Model model,
            @RequestParam(defaultValue = "") String title,
            @PageableDefault
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "noticeYn", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            })
            Pageable pageable
    ) {
        Page<PostDto> posts = postService.showAllPost(title, pageable);

        int nowPage = posts.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 2, 1);
        int endPage = Math.min(startPage + 4, posts.getTotalPages());

        model.addAttribute("posts", posts.getContent());
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPage", posts.getTotalPages());
        model.addAttribute("searchTitle", title);

        return "post/list";
    }

    // Read: 게시글 상세 조회
    @GetMapping("/{post_id}")
    public String showPostDetail(@PathVariable("post_id") Long postId, Model model) {
        PostDto post = postService.showPostDetail(postId);
        model.addAttribute("post", post);
//        model.addAttribute("comments", post.getComments());
        return "post/detail";
    }

    // Update: 게시글 수정
    @PutMapping("/{post_id}")
    public String updatePost(@PathVariable("post_id") Long postId, @RequestBody PostDto postDto) {
        postService.updatePost(postId, postDto);
        return "redirect:/post/" + postId;
    }

    // Delete: 게시글 삭제
    @PutMapping("/delete/{post_id}")
    public String deletePost(@PathVariable("post_id") Long postId) {
        postService.deletePost(postId);
        return "redirect:/post";
    }
}
