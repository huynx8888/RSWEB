
//	var docBase =  "http://www.nouchi.or.jp/WebContent/";
//	var docBase =  "http://www.nouchi.or.jp";
	var docBase =  "http://localhost:8080";
//	var docBase =  "http://192.168.100.2/GOURIKA";

	function init() {

		window.self.name="GourikaAndSolekia2011";

		var window_width = 1024;
		var window_height = 743;

		window.open( docBase + '/top/00.jsp','GOURIKA_MAIN','width='+window_width+',height='+window_height+',toolbar=0,location=0,directories=0,menubar=0,status=0,resizable=1,scrollbars=1');

		setTimeout('closeWindow()', 2000)
	}

	function openWindow( url ) {
		var window_width = 950;
		var window_height = 930;
		window.open(docBase + '/' + url , '_blank','width='+window_width+',height='+window_height+',toolbar=0,location=0,directories=0,menubar=0,status=0,resizable=1,scrollbars=1');
		return false;
	}

	function openWindow( url, windowName ) {
		var window_width = 950;
		var window_height = 930;
		var wname = windowName;
		if (wname == undefined) {
			wname = "_blank";
		}
		window.open(docBase + '/' + url , wname,'width='+window_width+',height='+window_height+',toolbar=0,location=0,directories=0,menubar=0,status=0,resizable=1,scrollbars=1');
		return false;
	}

	function closeWindow() {
		top.window.opener = top;
		top.window.open('','_parent','');
		top.window.close();
	}

	function init2() {
		//	location.replace("http://www.nouchi.or.jp/WebContent/top/00.html");
		//	location.replace("http://www.nouchi.or.jp/GOURIKA/top/00.html");
		location.replace(docBase + "/top/00.jsp");
	}
