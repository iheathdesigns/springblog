package com.techtalentsouth.techtalentblog.blogposts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class BlogPostController {
	
	//We can't construct this, we can't initialize this
	//We're going to ask spring boot to fill this value in
	//when our blogpost is created
	//We do this by adding the "@Autowired
	@Autowired
	private BlogPostRepository blogPostRepository;
	
	/*A request mapping as written here
	 * responds to all HTTP requests
	 * 
	 * GET/PUT/POST/DELETE/PATCH -- all will be processed
	 * by this mapping. We can specify that we only want to
	 * respond to GET methods by adding a second parameter labeled method
	 * and saying we respond to RequestMethod.GET
	 */
//	@RequestMapping(path="/", method = RequestMethod.GET)
//	private static List<BlogPost> posts = new ArrayList<>();
	
	@GetMapping(path="/")
	public String index(Model model) {
		//Since we're using the @Controller annotation
		//rather than the @RestController annotation
		//the String we are returning 
		//is actually a reference to a HTML template page
		
		//This model variable is a lot like a hashmap except all the keys
		//have to be strings
		List<BlogPost> posts = new ArrayList<>();
		for(BlogPost post: blogPostRepository.findAll()) {
			posts.add(post);
		};
		
//		BlogPost myBlogPost = new BlogPost();
//		
//		model.addAttribute("blogPost", myBlogPost);
		model.addAttribute("posts", posts);
		
		return "blogpost/index";
	}
	
	@GetMapping(path="/blogposts/new")
	public String newBlog(Model model) {
		model.addAttribute("blogPost", new BlogPost());
		return "blogpost/new";
	}
	
	@PostMapping(path="/blogposts")
	public String addNewBlogPost(BlogPost blogPost, Model model) {
		//We can now write the code to save the blog post to the DB
		//We need a blogPost repo and its an interface we can't create it
		//Only spring boot can
		blogPostRepository.save(blogPost);	
		model.addAttribute("blogPost", blogPost);
//		posts.add(blogPost);
//		model.addAttribute("title", blogPost.getTitle());
//		model.addAttribute("author", blogPost.getAuthor());
//		model.addAttribute("blogEntry", blogPost.getBlogEntry());
		return "blogpost/result";
	}
	/*If we specify a path with a variable name in curly braces, it becomes a wildcard 
	 * 
	 */
	@GetMapping(path="/blogposts/{id}")
	public String editPostWithId(@PathVariable Long id, Model model) {
		// We can now load in the blogpost with that ID
		Optional<BlogPost> postBox = blogPostRepository.findById(id);
		if(postBox.isPresent()) {
			BlogPost post = postBox.get();
			model.addAttribute("blogPost", post);
		}
		return "blogpost/edit";
	}
	
	@PostMapping(path="/blogposts/{id}")
	public String updateExistingPost(@PathVariable Long id, BlogPost blogPost, Model model) {
		Optional<BlogPost> postBox = blogPostRepository.findById(id);
		if(postBox.isPresent()) {
			BlogPost actualPost = postBox.get();
			actualPost.setTitle(blogPost.getTitle());
			actualPost.setAuthor(blogPost.getAuthor());
			actualPost.setBlogEntry(blogPost.getBlogEntry());
			
			blogPostRepository.save(actualPost); //Update the existing blogPost
			//This is an update rather than creating a new DB
			//entry because actualPost has the ID field
			model.addAttribute("blogPost", actualPost);
		}
		return "blogpost/result";
	}
	
	@GetMapping(path="/blogposts/delete/{id}")
	public String deletePostById(@PathVariable Long id) {
		blogPostRepository.deleteById(id);
		return "blogpost/delete";
	}
}
