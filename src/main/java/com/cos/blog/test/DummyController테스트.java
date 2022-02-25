package com.cos.blog.test;

import java.util.List;
import java.util.function.Supplier;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;


// html파일이 아니라 data를 리턴해주는 controller = RestController
@RestController
public class DummyController테스트 {
	
	@Autowired // 의존성 주입 (DI)
	private UserRepository userRepository;
	
	@DeleteMapping("/dummy/user/{id}") 
	public String delete(@PathVariable int id) {
		try {
			userRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			return "삭제에 실패하였습니다. 해당 id는 DB에 없습니다.";
		}		
		return "삭제되었습니다. id : " + id; 
	}
	
	// save함수는 id를 전달하지 않으면 insert를하고
	// save 함수는 id를 전달하면 해당 id에 데이터가 있으면 update, 없으면 insert를 한다
	// email, password
	@Transactional  // 함수 종료시에 자동 commit 이 됨.
	@PutMapping("/dummy/user/{id}")
	public User updateUser (@PathVariable int id, @RequestBody User requestUser) { //json 데이터를 요청 => Java Object(MessageConverte의 Jackson 라이브러리가 변환)로 변환해서 받아줌
		System.out.println("id : " + id);
		System.out.println("password : " + requestUser.getPassword());
		System.out.println("email : " + requestUser.getEmail());
		
		User user = userRepository.findById(id).orElseThrow(() -> {
			return new IllegalArgumentException("수정에 실패하였습니다.");
		});
		user.setPassword(requestUser.getPassword());
		user.setEmail(requestUser.getEmail());
		
//		userRepository.save(user); //@Transactional를 걸면 save를 하지 않아도 업데이트가 된다
		
		// 더티 체킹
		return user;
	}
	
	// http://localhost:8000/blog/dummy/user/5
	@GetMapping("/dummy/users")
	public List<User> list() {
		return userRepository.findAll();
	}
	
	// 한페이지당 2건의 데이터를 리턴받아 볼 예정
	//디테일하게 json으로 받아보는 버전
//	@GetMapping("/dummy/user")
//	public Page<User> pageList(@PageableDefault(size=2, sort="id", direction = Sort.Direction.DESC) Pageable pageable) {
//		Page<User> users = userRepository.findAll(pageable);
//		return users;
//	}
	@GetMapping("/dummy/user")
	public List<User> pageList(@PageableDefault(size=2, sort="id", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<User> pagingUser = userRepository.findAll(pageable);
		
		List<User> users = pagingUser.getContent();
		return users;
	}
	
	// {id} 주소로 파라미터를 전달 받을 수 있음.
	// http://localhost:8000/blog/dummy/user/5
	@GetMapping("/dummy/user/{id}")
	public User detail(@PathVariable int id) {
		// user/4를 찾는데 DB에 없어서 못찾아오게 되면 user가 null이 된다. 그러면 return null 이 되기 때문에 문제가 생김
		// 그래서 Optional로 User 객체를 감싸서 가져와서 null인지 아닌지 판단하서 return 시켜야 함
		
		// 1. 기본
		User user = userRepository.findById(id).orElseThrow(new Supplier<IllegalArgumentException>() {
			@Override
			public IllegalArgumentException get() {
				return new IllegalArgumentException("해당 유저는 없습니다. id : " + id);
			}
		});
		
		// 2. 람다식
//		User user = userRepository.findById(id).orElseThrow(()-> {		
//				return new IllegalArgumentException("해당 유저는 없습니다. id : " + id);
//			});
		// 요청 : 웹브라우저
		// user 객체 = 자바 오브젝트
		// 변환 (웹 브라우저가 이해할 수 있는 데이터) -> json (Gson 라이브러리를 사용하는 방법이 있지만..)
		// 스프링부트 = MessageConverter라는 애가 응답시에 자동 작동
		// 그래서 자바 오브젝트를 리턴하게 되면 MessageConverter가 Jackson 라이브러리를 호출해서
		// user 오브젝트를 json으로 변환해서 브라우저에 전달해준다
		return user;
	}
	
	// http://localhost:8000/blog/dummy/join (요청)
	// http의 body 에 username, password, email 데이터를 가지고 (요청)
	@PostMapping("/dummy/join")
	public String join(User user) { // key=value (약속된 규칙)
		System.out.println("username : " + user.getUsername());
		System.out.println("password : " + user.getPassword());
		System.out.println("email : " +user.getEmail());
		System.out.println("role : " +user.getRole());
		System.out.println("createDate : " +user.getCreateDate());
		
		user.setRole(RoleType.USER);
		userRepository.save(user);
		return "회원가입이 완료되었습니다.";
	}

}
