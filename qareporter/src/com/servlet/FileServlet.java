package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileServlet  extends HttpServlet{
	Logger logger = LoggerFactory.getLogger(FileServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -4127707481215744661L;
	/**
	   * Constructor of the object.
	   */
	  public FileServlet() {
	    super();
	  }

	  /**
	   * Destruction of the servlet. <br>
	   */
	  public void destroy() {
	    super.destroy(); // Just puts "destroy" string in log
	    // Put your code here
	  }

	  /**
	   * The doGet method of the servlet. <br>
	   *
	   * This method is called when a form has its tag value method equals to get.
	   * 
	   * @param request the request send by the client to the server
	   * @param response the response send by the server to the client
	   * @throws ServletException if an error occurred
	   * @throws IOException if an error occurred
	   */
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		  	this.doPost(request, response);
	  }

	  /**
	   * The doPost method of the servlet. <br>
	   *
	   * This method is called when a form has its tag value method equals to post.
	   * 
	   * @param request the request send by the client to the server
	   * @param response the response send by the server to the client
	   * @throws ServletException if an error occurred
	   * @throws IOException if an error occurred
	   */
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		  response.setContentType("application/json;charset=utf-8");
		  PrintWriter out = response.getWriter();
		  String type=request.getParameter("type");//获取类型
		  JSONObject jsonObject=null;//返回的
		  String jsonstr=request.getParameter("content");//内容
		  String token=request.getParameter("token");//email
		  logger.info("type="+type+",token="+token+",content="+jsonstr);
		  if(type.equals("getCataInfo")){
			  jsonObject=ServerInit.fileAction.getCataInfo(jsonstr);
		  }else if(type.equals("refreshCataInfo")) {
			  //jsonObject=ServerInit.fileAction.refreshCataInfo(jsonstr);
		  }else if(type.equals("delCata")) {
			  jsonObject=ServerInit.fileAction.delCata(jsonstr);
		  }else if(type.equals("changeTitle")) {
			  jsonObject=ServerInit.fileAction.changeTitle(jsonstr);
		  }
		  logger.info(jsonObject.toString());
		  out.println(jsonObject.toString());
		  out.flush();
		  out.close();
	  }

	  /**
	   * Initialization of the servlet. <br>
	   *
	   * @throws ServletException if an error occurs
	   */
	  public void init() throws ServletException {
	    // Put your code here
	  }
}
