package priv.xbchen.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
 
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import priv.wangao.LogAnalysis.algorithm.DrainParser;
import priv.wangao.LogAnalysis.service.SeqPredict;
import priv.wangao.LogAnalysis.util.IOHelper;
 
 

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
     
    // 上传文件存储目录
    private static final String UPLOAD_DIRECTORY = "upload";
 
    // 上传配置
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 1024 * 1;  // 1GB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 1024 * 5; // 5GB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 1024 * 5; // 5MB
 
    /**
     * 上传数据及保存文件
     */
    protected void doPost(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        // 检测是否为多媒体上传
        if (!ServletFileUpload.isMultipartContent(request)) {
            // 如果不是则停止
            PrintWriter writer = response.getWriter();
            writer.println("Error: 表单必须包含 enctype=multipart/form-data");
            writer.flush();
            return;
        }
 
        // 配置上传参数
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 设置临时存储目录
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
 
        ServletFileUpload upload = new ServletFileUpload(factory);
         
        // 设置最大文件上传值
        upload.setFileSizeMax(MAX_FILE_SIZE);
         
        // 设置最大请求值 (包含文件和表单数据)
        upload.setSizeMax(MAX_REQUEST_SIZE);
        
        // 中文处理
        upload.setHeaderEncoding("UTF-8"); 

        // 构造临时路径来存储上传的文件
        // 这个路径相对当前应用的目录
        String uploadPath = getServletContext().getRealPath("/") + File.separator + UPLOAD_DIRECTORY;
       
         
        // 如果目录不存在则创建
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        
        boolean flag= false;
 
        try {
            // 解析请求的内容提取文件数据
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);
            List<String> divSymbols = new ArrayList<String>();
            if (formItems != null && formItems.size() > 0) {
                // 迭代表单数据
            	String filePath = new String();
                for (FileItem item : formItems) {
                	//String value = new String(item.getString().getBytes("iso-8859-1"), "utf-8");
                	String name = item.getFieldName(); 
                	if (!name.equals("uploadFile")) {
                		System.out.println(name);
                		divSymbols.add(name);
                	}
                    // 处理不在表单中的字段
                	if (name.equals("uploadFile")) {//if (!item.isFormField()) {
                		flag =true;
                        String fileName = new File(item.getName()).getName();
                        filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);
                        // 保存文件到硬盘
                        item.write(storeFile);
                        // 在控制台输出文件的上传路径
                        System.out.println(filePath);
                        request.setAttribute("message",
                                "文件处理中，请稍后...");
                    }
                }
                DrainParser parser = new DrainParser(filePath, "E:\\drain");
                parser.entrance(generateSeparator(divSymbols));
                SeqPredict.getInstance().buildModel();
                List<String> eventList = IOHelper.getInstance().readFromFile("E:\\drain\\event.log");
                request.getSession().setAttribute("events", eventList);
            }
        } catch (Exception ex) {
            request.setAttribute("message",
                    "错误信息: " + ex.getMessage());
        }
        // 跳转回 home.jsp
        //getServletContext().getRequestDispatcher("/home.jsp?#work").forward(request, response);
        response.sendRedirect("/LogParser/home.jsp?#logevents");
    }
    
    
    private String generateSeparator(List<String> divSymbols) {
    	if (divSymbols.size() == 0) {
    		return "[\\s]+";
    	}
    	StringBuilder sb =  new StringBuilder("[");
    	for (int i = 0; i < divSymbols.size(); i++) {
    		switch(divSymbols.get(i)) {
    		case "blank" : sb.append(" "); break;
    		case "equal" : sb.append("="); break;
    		case "colon" : sb.append(":"); break;
    		case "enter" : sb.append("\\n"); break;
    		}
    	}
    	sb.append("]+");
    	System.out.println(sb.toString());
    	return sb.toString();
    	
    }
}