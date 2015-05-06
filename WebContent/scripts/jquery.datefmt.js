/*
<html>
<head>
<script type="text/javascript" src="jquery.js"></script>
<script type="text/javascript" src="jquery.datefmt.js"></script>
<script type="text/javascript" language="javascript">
<!--
$(function(){
	$("input[name='date1'],input[name='date2']").datefmt();
});
//-->
</script>
</head>
<body>
	<form>
		<input type="text" name="text1" value="text1"><br>
		<input type="text" name="date1" value="2013/03/01"><br>
		<input type="text" name="text2" value="text2"><br>
		<input type="text" name="date2" value="2013/03/31"><br>
	</form>
</body>
</html>
*/
(function($){
	$.fn.datefmt = function(){
		return this.each(function(){
			$(this).focus(function(){
				$(this).val($(this).val().replace(/\//g,""));
				$(this).select();
			});
			$(this).blur(function(){
				if ($(this).val().match(/^(\d{8})$/)) {
					$(this).val([$(this).val().substring(0,4),$(this).val().substring(4,6),$(this).val().substring(6,8)].join("/"));
				}
			});
		});
	};
})(jQuery);
