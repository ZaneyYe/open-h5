package com.servlet;

import com.test.OpenH5User;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Servlet implementation class FindUser
 */
@WebServlet("/BindUser")
public class BindUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(BindUser.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BindUser() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String result = null;
		String InsUsrId = request.getParameter("userId");
		String name = request.getParameter("name");
		String certifId = request.getParameter("certifId");
		String mobile = request.getParameter("mobile");
		String card = request.getParameter("cardNo");
		List<String> cardNos = Collections.singletonList(card);
		LOGGER.info("request userId:" + InsUsrId + ",certifId：" + certifId + ",mobile:" + mobile + ",card:" + card);
		try {
			result = OpenH5User.binduser(InsUsrId, name, certifId, cardNos, mobile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.getWriter().print(result);
	}

}
