package com.KHCafeErp.www.service.impl;

import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.servlet.http.HttpServletRequest;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.KHCafeErp.www.dao.face.JoinStaffDao;
import com.KHCafeErp.www.dto.Staff;
import com.KHCafeErp.www.service.face.JoinStaffService;



@Service
public class JoinStaffServiceImpl implements JoinStaffService {

	@Autowired private JavaMailSender mailSender;
	@Autowired private JoinStaffDao joinStaffDao;
	
	@Override
	public void join(Staff staff) {
		
		joinStaffDao.insert(staff);
		
	}

	@Override
	public int nickCheck(String nick) {
		
		int result = joinStaffDao.nickCheck(nick);
		return result;
	}

	@Override
	public int idCheck(int staffNo) {
		return joinStaffDao.selectIdCntCheck(staffNo);
	}

	// 이메일 난수 만드는 메서드
	private String init() {
		Random ran = new Random();
		StringBuffer sb = new StringBuffer();
		int num = 0;

		do {
			num = ran.nextInt(75) + 48;
			if ((num >= 48 && num <= 57) || (num >= 65 && num <= 90) || (num >= 97 && num <= 122)) {
				sb.append((char) num);
			} else {
				continue;
			}

		} while (sb.length() < size);
		if (lowerCheck) {
			return sb.toString().toLowerCase();
		}
		return sb.toString();
	}

	// 난수를 이용한 키 생성
	private boolean lowerCheck;
	private int size;

	public String getKey(boolean lowerCheck, int size) {
		this.lowerCheck = lowerCheck;
		this.size = size;
		return init();
	}

	@Override
	public void mailSendWithjoinStaffKey(String email, int staffNo, HttpServletRequest req) {

		// 난수를 이용한 키 생성
		String key = getKey(false, 20);
		
		joinStaffDao.getKey(staffNo, key); 
		
		MimeMessage mail = mailSender.createMimeMessage();
		String htmlStr = "<h2>안녕하세요 MS :p KH-CAFE 입니다!</h2><br><br>" 
				+ "<h3>" + staffNo + "님</h3>" + "<p>인증하기 버튼을 누르시면 로그인을 하실 수 있습니다 : " 
				+ "<a href='http://localhost:8080" + req.getContextPath() + "/joinStaff/key_alter?joinStaff_id="+ staffNo +"&joinStaff_key="+key+"'>인증하기</a></p>"
				+ "(혹시 잘못 전달된 메일이라면 이 이메일을 무시하셔도 됩니다)";
		try {
			mail.setSubject("[본인인증] MS :p 민수르님의 인증메일입니다", "utf-8");
			mail.setText(htmlStr, "utf-8", "html");
			mail.addRecipient(RecipientType.TO, new InternetAddress(email));
			mailSender.send(mail);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
	}

	// 인증 확인 메서드 (Y 값으로 바꿔주는 메서드)
	public int alter_userKey_service(int staffNo, String key) {

		int resultCnt = 0;
		resultCnt = joinStaffDao.alterJoinStaffKey(staffNo, key);

		return resultCnt;
	}


}
