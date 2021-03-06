package com.cos.blog.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// ORM -> JAVA(뿐만아니라 모든 언어) object -> 테이블로 매핑해주는기술
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // 빌더 패턴!!
// @DynamicInsert  //insert할때 null 인 필드를 제외한다
@Entity // User 클래스가 MySQL에 테이블이 생성이된다.
public class User {
	
	@Id //기본키 Primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY) //프로젝트에서 연결된 DB의 넘버링 전략을 따라간다.(ex 오라클이면 오라클의 mysql이면 mysql의 전략을 따라감)
	private int id; // 시퀀스, auto_increment 
	
	@Column(nullable = false , length = 100, unique = true)
	private String username; // 아이디
	
	@Column(nullable = false , length = 100) // 123456 => 해쉬(비밀번호 암호화)로 인해 길이를 길게해야함
	private String password; 
	
	@Column(nullable = false , length = 50)
	private String email;
	
//	@ColumnDefault("USER")
	// DB는 RoleType이라는 게 없어서 에노테이션 추가해야함
	@Enumerated(EnumType.STRING)
	private RoleType role; // Enum을 쓰는게 좋다.(고정된 설정을 위한 도메인 설정 가능) // ADMIN, USER, MANAGER 권한을 주기 위해 사용
	
	private String oauth; // kakao, google
	
	@CreationTimestamp // 시간이 자동으로 입력이 됨
	private Timestamp createDate;
}
