<%--
  User: eurohlam
  Date: 13/02/2018
--%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<header class="header clearfix">
	<nav id="mainNav" class="navbar navbar-default navbar-fixed-top navbar-custom">
        <div class="container">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header page-scroll">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                    <span class="sr-only">Toggle navigation</span> Menu <i class="fa fa-bars"></i>
                </button>
                <a class="navbar-brand" href="#page-top">RSS-2-KINDLE</a>
            </div>

            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav navbar-right">
                    <li class="hidden">
                        <a href="#page-top"></a>
                    </li>
			        <form id="logout" action="<c:url value="logout"/>" method="post">
		                <li role="presentation" class = "dropdown active">
        		            <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
        	        	        <%=username%> <span class="caret"></span>
		                    </a>
            		        <ul class="dropdown-menu">
                		        <li role="presentation" class="page-scroll">
                    		        <a href="javascript:{}" onclick="document.getElementById('logout').submit(); return false;">Log out</a>
                        		</li>
		                    </ul>
    		            </li>
                        <security:csrfInput/>
					</form>

                    <li class="page-scroll">
                        <a href="../index.html#portfolio">Portfolio</a>
                    </li>
                    <li class="page-scroll">
                        <a href="../index.html#about">About</a>
                    </li>
                    <li class="page-scroll">
                        <a href="../index.html#contact">Contact</a>
                    </li>
                </ul>
            </div>
            <!-- /.navbar-collapse -->
        </div>
        <!-- /.container-fluid -->
    </nav>
    <!--nav>
        <form id="logout" action="<c:url value="logout"/>" method="post">
            <ul class="nav nav-pills pull-right">
                <li role="presentation" class = "dropdown active">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                        <%=username%> <span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu">
                        <li role="presentation">
                            <a href="javascript:{}" onclick="document.getElementById('logout').submit(); return false;">Log out</a>
                        </li>
                    </ul>
                </li>
                <li role="separator" class="divider"></li>
                <li role="presentation"><a href="../index.html">Home</a></li>
                <li role="presentation"><a href="../about.html">About</a></li>
                <li role="presentation"><a href="../contact.html">Contact</a></li>
            </ul>
            <security:csrfInput/>
        </form>
    </nav>
    <h3 class="text-muted">RSS-2-KINDLE</h3-->
</header>
