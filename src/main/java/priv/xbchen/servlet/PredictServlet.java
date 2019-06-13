package priv.xbchen.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import priv.wangao.LogAnalysis.constant.Common;
import priv.wangao.LogAnalysis.service.SeqPredict;
import priv.wangao.LogAnalysis.util.IOHelper;

@WebServlet("/PredictServlet")
public class PredictServlet extends HttpServlet {
	 protected void doPost(HttpServletRequest request,
	            HttpServletResponse response) throws ServletException, IOException {
	    	
	    	String sequence = request.getParameter("sequence");
	    	String isNum = request.getParameter("isNum");
	    	String[] split = sequence.split(Common.LOCAL_LINE_SEPARATOR);
//	    	System.out.println(split.length);
//	    	for (int i = 0; i < split.length; i++) {
//	    		System.out.println(split[i]);
//	    	}
	    	 List<String> res = new ArrayList<String>();
	    	if (isNum.equals("yes")) {
	    		res = SeqPredict.getInstance().predict(split);
	    	}
	    	
	    	if (isNum.equals("no")) {
	    		String[] numSeq = SeqPredict.getInstance().logToNum(split);
	    		res = SeqPredict.getInstance().predict(numSeq);
	    	}
	    	
	    	request.getSession().setAttribute("result", res);
	    	//getServletContext().getRequestDispatcher("/home.jsp?#predict").forward(request, response);
	    	response.sendRedirect("/LogParser/home.jsp?#predict");
	    }
}
