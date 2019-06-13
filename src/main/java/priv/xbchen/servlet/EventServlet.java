package priv.xbchen.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import priv.wangao.LogAnalysis.util.IOHelper;

@WebServlet("/EventServlet")
public class EventServlet extends HttpServlet{
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
    	String id = request.getParameter("id");
    	List<String> eventList = IOHelper.getInstance().readFromFile("E:\\drain\\event.log");
    	List<String> regexList = IOHelper.getInstance().readFromFile("E:\\drain\\group\\groups.log");
    	List<String> logList = IOHelper.getInstance().readFromFile("E:\\drain\\group\\" + id +".log");
        request.getSession().setAttribute("logs", logList);
    	request.setAttribute("event", eventList.get(Integer.parseInt(id) - 1));
    	request.setAttribute("regex", regexList.get(Integer.parseInt(id) - 1));
    	getServletContext().getRequestDispatcher("/Message.jsp").forward(request, response);
    }
}
