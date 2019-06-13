<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*"%>
<!DOCTYPE html>
<html lang="en" class="no-js">
    <head>
        <meta charset="utf-8"/>
        <title>Home</title>
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta content="width=device-width, initial-scale=1" name="viewport"/>
        <link href="http://fonts.googleapis.com/css?family=Hind:300,400,500,600,700" rel="stylesheet" type="text/css">
		<link href="css/font-awesome.css" rel="stylesheet">
        <link href="vendor/simple-line-icons/simple-line-icons.min.css" rel="stylesheet" type="text/css"/>
        <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
        <link href="css/animate.css" rel="stylesheet">
        <link href="vendor/swiper/css/swiper.min.css" rel="stylesheet" type="text/css"/>
        <link href="css/layout.min.css" rel="stylesheet" type="text/css"/>
        <link rel="shortcut icon" href="favicon.ico"/>
        
        <style>
			p.normal {
			 font-size: 15px;
			 font-weight: 400;
			 font-family: Hind, sans-serif;
			 color: black;
			 line-height: 1.4;
			 margin-bottom: 15px;
			}
			p.type1 {
				font-size: 16px;
				font-weight: 500;
				font-family: "Microsoft YaHei";
				color: black;
				line-height: 1.4;
				margin-bottom: 15px;
			}
			p.type2 {
				font-size: 15px;
				font-weight: 600;
				font-family: "Microsoft YaHei";
				color: black;
				line-height: 1.4;
				margin-bottom: 15px;
			}
			a.jump {
				font-size: 15px;
				font-weight: 600;
				font-family: "Microsoft YaHei";
				color: white;
				line-height: 1.4;
				margin-bottom: 15px;
			}
			textarea.input{
				font-size: 15px;
				font-weight: 600;
				font-family: "Microsoft YaHei";
				color: black;
				line-height: 1.4;
				margin-bottom: 15px;
			}
		</style>
    </head>
   
  
    <body id="body" data-spy="scroll" data-target=".header">
       	<script language="JavaScript">

		function checkForm(){
			var form = document.getElementById("uploadForm");
			if(form.uploadFile.value == "")
			{
				alert("请选择文件!");
			}else{
				form.action="/LogParser/UploadServlet";
				var lDiv = document.getElementById("loading");
				if(lDiv.style.display=='none'){
				    lDiv.style.display='block';
				}
			}
		}
		</script>
		<%List<String> events = (List<String>)session.getAttribute("events"); %>
          <header class="header navbar-fixed-top">
              <nav class="navbar" role="navigation">
                <div class="container">
                      <div class="menu-container js_nav-item">
                        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".nav-collapse">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="toggle-icon"></span>
                        </button>

                          <div class="logo">
                            <a class="logo-wrap" href="#body">
                                <img class="logo-img logo-img-main" src="img/logo.png" alt="FlameOnePage Logo">
                                <img class="logo-img logo-img-active" src="img/logo-dark.png" alt="FlameOnePage Dark Logo">
                            </a>
                        </div>
                    </div>

                    <div class="collapse navbar-collapse nav-collapse">
					
					<!--div class="language-switcher">
					  <ul class="nav-lang">
                        <li><a class="active" href="#">EN</a></li>
					    <li><a href="#">DE</a></li>
						<li><a href="#">FR</a></li>
					  </ul>
					</div---> 
					
                        <div class="menu-container">
                            <ul class="nav navbar-nav navbar-nav-right">
                                <li class="js_nav-item nav-item"><a class="nav-item-child nav-item-hover" href="#body">Home</a></li>
                                <li class="js_nav-item nav-item"><a class="nav-item-child nav-item-hover" href="#extract">Extract</a></li>
                                <li class="js_nav-item nav-item"><a class="nav-item-child nav-item-hover" href="#logevents">LogEvents</a></li>
								<li class="js_nav-item nav-item"><a class="nav-item-child nav-item-hover" href="#predict">Predict</a></li>
                                <!--<li class="js_nav-item nav-item"><a class="nav-item-child nav-item-hover" href="#products">Products</a></li>
                                <li class="js_nav-item nav-item"><a class="nav-item-child nav-item-hover" href="#pricing">Pricing</a></li>
                                <li class="js_nav-item nav-item"><a class="nav-item-child nav-item-hover" href="#contact">Contact</a></li>--> 
                            </ul>
                        </div>
                    </div>
                </div>
            </nav>
			</header>
       
        <div id="carousel-example-generic" class="carousel slide" data-ride="carousel">

            <div class="carousel-inner" role="listbox">
                <div class="item active">
                    <img class="img-responsive" src="img/1920x1080/01.jpg" alt="Slider Image">
                    <div class="container">
                        <div class="carousel-centered">
                            <div class="margin-b-40">
                                <h1 class="carousel-title">日志格式提取、日志序列预测</h1>
                                <p class="color-white">Collecting and analyzing logs <br/>A better way to manage your system</p>
                            </div>
                            <a href="#extract" class="btn-theme btn-theme-sm btn-white-brd text-uppercase">Begin</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!--========== SLIDER ==========-->

        <!--========== PAGE LAYOUT ==========-->
        <!-- About -->
        <div id="extract">
            <div class="bg-color-sky-light">
                <div class="content-lg container">
                    <div class="row">
                        <div class="col-md-5 col-sm-5 md-margin-b-60">
                            <div class="margin-t-50 margin-b-30">
                                <h2>请选择一个日志文件</h2>
                                <li>文件大小限制为5GB</li>
                                <li>文件中的一行认为是一条日志</li>
                                <br/>
                            </div>
                           <form id = "uploadForm" method="post" enctype="multipart/form-data">
							<input type="file" name="uploadFile"/>
							<br/>
							<h4>请指定日志分隔符(默认为任何空白符):</h4>
							<p class = "type1"><input type="checkbox" name="blank" />空格(" ") 
							<input type="checkbox" name="equal" /> 等号("=") 
							<input type="checkbox" name="colon" /> 冒号(":") 
							<!--<input type="checkbox" name="enter" /> 回车("\n") -->
							</p>
							<br/><br/>
							<input type="submit" class="btn-theme btn-theme-sm btn-white-bg text-uppercase" value="process" onclick = "checkForm()"/>
							</form>
							<br/>
							<div id="loading" style="display:none">
    							<img src="img/loading.gif">
							</div> 
                        </div>
                        <div class="col-md-5 col-sm-7 col-md-offset-2">
                        	<h2>Extract</h2>
                            <img src="img/extract.jpg">
                        </div>
                    </div>
                    <!--// end row -->
                </div>
            </div>
        </div>
        <!-- End About -->
		<%if (events != null){%>
        <!-- Work -->
        <div id="logevents">
            <div class="section-seperator">
                <div class="content-md container">
                    <div class="row margin-b-40">
                        <div class="col-sm-6">
                            <h2>格式提取结果</h2>
                        </div>
                    </div>
                    <!--// end row -->
					<p class = "normal"> 每个日志格式中“*”代表此处是一个变量值。每个日志格式也代表了相同格式的一组日志。</p>
                    <p class = "normal"> 点击"Go Predict"按钮可进入日志序列预测界面</p> 
                    <a href="#predict" class = "jump btn-theme btn-theme-sm btn-white-bg text-uppercase">Go predict</a>
                    <p></p>
					<p class = "type1">一共整理出<%=events.size()%>个日志格式。</p> 
					<p class = "normal"> 点击"详细信息"按钮可查看该日志格式的详细信息</p> <% 
					for (int i = 0; i < events.size(); i++){
						String event = (String)events.get(i);
						%> <br/> <p class = "type2"> <%=String.valueOf(i+1) %>: <%= event %></p>
						<form method="post" action="/LogParser/EventServlet">
						<input type ="hidden" name = "id" value = <%=String.valueOf(i+1) %> />
						<input type="submit"  class="btn-theme btn-theme-sm btn-white-bg text-uppercase" value="详细信息"/>
						</form><%
					}%>
                </div>
            </div>
        </div>
        <!-- End Work -->

		<!-- Services -->
        <div id="predict">
            <div class="bg-color-sky-light" data-auto-height="true">
                <div class="content-lg container">
                    <div class="row margin-b-40">
                        <div class="col-sm-6">
                            <h2>日志序列预测</h2>
                            <!-- Accordion -->
                            <div class="accordion">
                                <div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
                                    <div class="panel panel-default">
                                        <div class="panel-heading" role="tab" id="headingOne">
                                            <h4 class="panel-title">
                                                <a class="panel-title-child" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                                                                                              输入日志序列
                                                </a>
                                            </h4>
                                        </div>
                                        <div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
                                            <div class="panel-body">
                                            	<p class = "normal">日志之间以回车分隔</p>
                                            	<form name = "predictForm1" action= "/LogParser/PredictServlet" method="post">
                                            		<input type ="hidden" name = "isNum" value = "no" />
                                               		<textarea class="input" name = "sequence" style="OVERFLOW: visible color:black" cols="45" rows="6"></textarea>
                                            		<input type="submit"  class="btn-theme btn-theme-sm btn-white-bg text-uppercase" value="Predict"/>
                                            	</form>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="panel panel-default">
                                        <div class="panel-heading" role="tab" id="headingTwo">
                                            <h4 class="panel-title">
                                                <a class="collapsed panel-title-child" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
                                                                                              输入日志格式编号序列
                                                </a>
                                            </h4>
                                        </div>
                                        <div id="collapseTwo" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingTwo">
                                            <div class="panel-body">
                                            	<p class = "normal">编号之间以回车分隔</p>
                                                <form name = "predictForm2" action= "/LogParser/PredictServlet" method="post">
                                            		<input type ="hidden" name = "isNum" value = "yes" />
                                               		<textarea class="input" name = "sequence" style="OVERFLOW: visible color:black" cols="45" rows="6"></textarea>
                                            		<input type="submit"  class="btn-theme btn-theme-sm btn-white-bg text-uppercase" value="Predict"/>
                                            	</form>
                                            </div>
                                        </div>
                                    </div>
                                    </div>
                                </div>
                            </div>
                            <!-- End Accodrion -->
                            <% List<String> result = ( List<String>)session.getAttribute("result"); 
                            if (result != null) {%>
	                            <div class="col-sm-6">
	                            	<h2>预测结果</h2>
	                                <% for (int i = 0; i < result.size(); i++){ %>
	                                	<br/> 
	                                	<p class = "type2"> <%= result.get(i) %></p>
	                				<%} %>
	                            </div>
                            <%} %>
                        </div>
                </div>
                    <!--// end row -->
                </div>
            </div>
        <%}%>
        <!-- End Service -->
        <!--========== FOOTER ==========-->
        <footer class="footer">
            <!-- Links -->
            <div class="section-seperator">
                <div class="content-md container">
                    <div class="row">
                        <div class="col-sm-2 sm-margin-b-30">
                            <!-- List -->
                            <ul class="list-unstyled footer-list">
                                <li class="footer-list-item"><a href="#body">Home</a></li>
                                <li class="footer-list-item"><a href="#about">Team</a></li>
                                <li class="footer-list-item"><a href="#work">Credentials</a></li>
                                <li class="footer-list-item"><a href="#contact">Contact</a></li>
                            </ul>
                            <!-- End List -->
                        </div>
                        <div class="col-sm-2 sm-margin-b-30">
                            <!-- List -->
                            <ul class="list-unstyled footer-list">
                                <li class="footer-list-item"><a href="#">Twitter</a></li>
                                <li class="footer-list-item"><a href="#">Facebook</a></li>
                                <li class="footer-list-item"><a href="#">Instagram</a></li>
                                <li class="footer-list-item"><a href="#">YouTube</a></li>
                            </ul>
                            <!-- End List -->
                        </div>
                        <div class="col-sm-3">
                            <!-- List -->
                            <ul class="list-unstyled footer-list">
                                <li class="footer-list-item"><a href="#">Subscribe to Our Newsletter</a></li>
                                <li class="footer-list-item"><a href="#">Privacy Policy</a></li>
                                <li class="footer-list-item"><a href="#">Terms &amp; Conditions</a></li>
                            </ul>
                            <!-- End List -->
                        </div>
                    </div>
                    <!--// end row -->
                </div>
            </div>
            <!-- End Links -->

            <!-- Copyright -->
            <div class="content container">
                <!--// end row -->
            </div>
            <!-- End Copyright -->
        </footer>
        <!--========== END FOOTER ==========-->

        <!-- Back To Top -->
        <a href="javascript:void(0);" class="js-back-to-top back-to-top">Top</a>

        <!-- JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
        <!-- CORE PLUGINS -->
        <script src="vendor/jquery.min.js" type="text/javascript"></script>
        <script src="vendor/jquery-migrate.min.js" type="text/javascript"></script>
        <script src="vendor/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>

        <!-- PAGE LEVEL PLUGINS -->
        <script src="vendor/jquery.easing.js" type="text/javascript"></script>
        <script src="vendor/jquery.back-to-top.js" type="text/javascript"></script>
        <script src="vendor/jquery.smooth-scroll.js" type="text/javascript"></script>
        <script src="vendor/jquery.wow.min.js" type="text/javascript"></script>
        <script src="vendor/swiper/js/swiper.jquery.min.js" type="text/javascript"></script>
        <script src="vendor/masonry/jquery.masonry.pkgd.min.js" type="text/javascript"></script>
        <script src="vendor/masonry/imagesloaded.pkgd.min.js" type="text/javascript"></script>

        <!-- PAGE LEVEL SCRIPTS -->
        <script src="js/layout.min.js" type="text/javascript"></script>
        <script src="js/components/wow.min.js" type="text/javascript"></script>
        <script src="js/components/swiper.min.js" type="text/javascript"></script>
        <script src="js/components/masonry.min.js" type="text/javascript"></script>

    </body>
    <!-- END BODY -->
</html>